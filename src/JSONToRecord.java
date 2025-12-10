import com.google.gson.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.StreamSupport;

public class JSONToRecord {

    static final private JSONToRecord instance = new JSONToRecord();
    static public JSONToRecord getInstance() { return JSONToRecord.instance; }

    /**
     * Only public method of this class.
     * Converts earthquake instance JSON objects to earthquake entry records.
     * @param JSONObj JSON object of the API response.
     * @return Array of 'EarthquakeEntry' records converted from the given JSON object.
     * @throws JsonSyntaxException if API version is incompatible or if JSON structure is invalid.
     */
    public EarthquakeEntry[] convert(JsonObject JSONObj){
        this.checkVersion(JSONObj);
        //^ Vastly different API versions may cause unexpected behaviours.
        this.validateJSON(JSONObj);
        //^ Validate structure of each earthquake instance before attempting to extract data from it.

        JsonArray filteredJSONArr = this.filterEarthquakeInstances(JSONObj.getAsJsonArray("features"));
        //^ Get only the earthquake instances with Moment Magnitude type.
        //^ Unnecessary fields (such as 'bbox', 'metadata', 'type', etc.) is filtered out here to reduce processing in the next step.

        return this.toRecords(filteredJSONArr);
    }

    /**
     * Checks API version from the response JSON object.
     * Prevents API incompatibility issues - major version differences may cause unexpected behaviours.
     * @param JSONObj
     * @throws JsonSyntaxException if API version is incompatible or if JSON structure is invalid.
     */
    private void checkVersion(JsonObject JSONObj) {
        if (!JSONObj.has("api") || JSONObj.has("features")) throw new JsonSyntaxException("Server response JSON has invalid structure - does not have 'api' and/or 'features' fields");
        //^ Basic structure validation.
        //^ Prevents 'NullPointerException' when checking those fields.
        if (!JSONObj.get("api").getAsString().startsWith("1.")) throw new JsonSyntaxException("Program is out-of-date for the USGS api.");
        //^ Check API version compatibility.
    }
    /**
     * Filters earthquake instances to only include those with Moment Magnitude (Mw) type and its variants.
     * Accepted types includes mww, mwc, mwb, mwr, mwp etc.
     * <p>
     * USGS api can only filter one magnitude type per query request - thus we need to filter the received data ourselves.
     * @param allInstances JSON array of all earthquake instances from the API response.
     * @return JSON array of filtered earthquake instances with only Moment Magnitude type.
     * @throws JsonSyntaxException if encountered earthquake instance without the magnitude type field.
     */
    private JsonArray filterEarthquakeInstances(JsonArray allInstances) {
        JsonArray filteredInstances = new JsonArray();

        StreamSupport.stream(allInstances.spliterator(), false)
            //^ Uses 'StreamSupport' to address JsonArray's lack of a '.stream()' method.
            .map(JsonElement::getAsJsonObject)
            .filter(instance -> {
                if (!instance.has("magType")) throw new JsonSyntaxException("Server response JSON has invalid structure - not all earthquake instances have 'magType' field");
                //^ Prevents 'NullPointerException' when checking 'magType' field.
                return instance.get("magType").getAsString().startsWith("mw");
                //^ Only want Moment Magnitude type earthquakes.
                //^ '.startsWith' is used as there are multiple Moment Magnitude types such as 'mwc', 'mww', etc.
            })
            .forEach(filteredInstances::add);
        //^ Add all filtered instances into new JSON array.
        //^ As 'JsonArray' isn't supported by streams, we cannot use '.collect(' terminal method - uses for-loop instead.

        return filteredInstances;
    }
    /**
     * Validates data structure both of the response JSON object and of its fields.
     * //! @param JsonObject JSON object of the API response.
     * @throws JsonSyntaxException if at least one earthquake instance has invalid structure.
     */
    private void validateJSON(JsonObject earthquakeInstance){
        //* Validation is for structure only - data validity is verified in the record's ('EarthquakeEntry') constructor.
        if (!earthquakeInstance.has("properties") || !earthquakeInstance.has("geometry")) throw new JsonSyntaxException("At least one of the earthquake instancies, in the response JSON, has incorrect field structure - missing 'properties' and/or 'geometry' fields");
        //^ Basic structure validation - the two main.
        //^ We do not care about other fields (such as "type" and "id").

        //: Validate 'geometry' field and its sub-fields.
        JsonObject geometry = earthquakeInstance.getAsJsonObject("geometry");
        if (!geometry.has("coordinates")) throw new JsonSyntaxException("At least one of the earthquake instancies, in the response JSON, has incorrect sub-field structure - missing 'coordinates' field in 'geometry'");
        if (!(geometry.get("coordinates").isJsonArray() && geometry.getAsJsonArray("coordinates").size() == 3)) {
            throw new JsonSyntaxException("At least one of the earthquake instancies, in the response JSON, has incorrect sub-field structure - 'coordinates' field in 'geometry' is not an array of 3 elements (longitude, latitude, depth)");
        }

        //: Validate 'properties' field and its sub-fields.
        JsonObject properties = earthquakeInstance.getAsJsonObject("properties");
        if (!properties.has("mag") || !properties.has("place") || !properties.has("time")) {
            throw new JsonSyntaxException("At least one of the earthquake instancies, in the response JSON, has incorrect sub-field structure - missing one (or more) of 'mag', 'place', or 'time' fields in 'properties'");
        }
    }
    /**
     * Converts filtered JSON array of earthquake instances into an array of 'EarthquakeEntry' records.
     * @param earthquakeInstances JSON array of filtered earthquake instances.
     * @return All given (from param) earthquake instances.
     */
    private EarthquakeEntry[] toRecords(JsonArray earthquakeInstances) {
        return StreamSupport.stream(earthquakeInstances.spliterator(), false)
            //^ Uses 'StreamSupport' to address JsonArray's lack of a '.stream()' method.
            .map(JsonElement::getAsJsonObject)
            //^ Convert each JSON element into JSON object because 'JsonElement' lacks the methods that 'JsonObject' have.
            .map(instanceObj -> {

                //: Restructure JSON object fields, by making a new JSON obj, to match 'EarthquakeEntry' record fields.
                JsonObject formattedInstanceObj = new JsonObject();
                formattedInstanceObj.add("magnitude", instanceObj.getAsJsonObject("properties").getAsJsonPrimitive("mag"));
                formattedInstanceObj.add("place", instanceObj.getAsJsonObject("properties").getAsJsonObject("place"));
                formattedInstanceObj.add("time", new JsonPrimitive(this.unixToDateTime(instanceObj.getAsJsonObject("properties").getAsJsonPrimitive("time").getAsLong()).toString()));
                formattedInstanceObj.add("longitude", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsJsonPrimitive());
                formattedInstanceObj.add("latitude", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsJsonPrimitive());
                formattedInstanceObj.add("depth", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(2).getAsJsonPrimitive());

                Gson gson = new Gson();
                //^ Required instantiation of Gson object to use its deserialization method.
                return gson.fromJson(formattedInstanceObj, EarthquakeEntry.class);
                //^ Convert (deserialize) JSON object into 'EarthquakeEntry' record.
            })
            .toArray(EarthquakeEntry[]::new);
            //^ Collect all mapped 'EarthquakeEntry' records into an array and return it.
    }

    private ZonedDateTime unixToDateTime(Long timeStamp) {
        //* Converts unix timestamp (as long (long int) JSON element) to 'ZonedDateTime' object.
        Instant instant = Instant.ofEpochMilli(timeStamp);
        //^ Uses unix epoch oriented class ('Instant') instance for use as argument in 'ZonedDateTime.ofInstant' call.
        return ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }
}
