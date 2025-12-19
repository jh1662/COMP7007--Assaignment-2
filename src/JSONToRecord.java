import com.google.gson.*;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

/**
 * Allows converting JSON objects, of earthquake instances, into 'EarthquakeEntry' records.
 * <p>
 * Implements singleton pattern to ensure only one instance exists.
 */
public class JSONToRecord {
    //: Singleton pattern implementation.
    static final private JSONToRecord instance = new JSONToRecord();
    static public JSONToRecord getInstance() { return JSONToRecord.instance; }

    /**
     * Validates basic structure of the response JSON object.
     * Ensures required fields are present, and of correct types.
     * @param JSONObj api response JSON object.
     * @throws JsonSyntaxException if JSON structure is invalid.
     */
    public void validateResponseStructure(JsonObject JSONObj){
        if (!JSONObj.has("features")) throw new JsonSyntaxException("Server response JSON has invalid structure - does not have 'features' field");
        //^ "features" field has Earthquake instances array.
        if (!JSONObj.get("features").isJsonArray()) throw new JsonSyntaxException("Server response JSON has invalid structure - 'features' field is not a JSON array");
        //^ "features" field must be a JSON array.
        if (JSONObj.getAsJsonArray("features").isEmpty()) throw new NoSuchElementException("Query responded with no Earthquake instances therefore cannot process data - 'features' array is empty");
        //^ Ensure there is at least one earthquake instance to process.
    }

    /**
     * Only public method of this class.
     * Converts earthquake instance JSON objects to earthquake entry records.
     * @param JSONResponse JSON object of the API response.
     * @return Array of 'EarthquakeEntry' records converted from the given JSON object.
     * @throws JsonSyntaxException if API version is incompatible or if JSON structure is invalid.
     */
    public EarthquakeEntry[] convert(JsonObject JSONResponse){
        this.checkVersion(JSONResponse);
        //^ Vastly different API versions may cause unexpected behaviours.

        this.validateResponseStructure(JSONResponse);
        //^ Validate basic structure of the response JSON object - check fields.

        StreamSupport.stream(JSONResponse.getAsJsonArray("features").spliterator(), false)
            //* Validate structure of each earthquake instance before attempting to extract data from it.
            .map(JsonElement::getAsJsonObject)
            //^ Convert each JSON element into JSON object because 'JsonElement' lacks the methods that 'JsonObject' have.
            .forEach(this::validateEarthquakeInstance);
            //^ The actual validation call for each earthquake instance.

        JsonArray filteredJSONArr = this.filterEarthquakeInstances(JSONResponse.getAsJsonArray("features"));
        //^ Get only the earthquake instances with Moment Magnitude type.
        //^ Unnecessary fields (such as 'bbox', 'metadata', 'type', etc.) is filtered out here to reduce processing in the next step.

        return this.toRecords(filteredJSONArr);
    }

    /**
     * Checks API version from the response JSON object.
     * Prevents API incompatibility issues - major version differences may cause unexpected behaviours.
     * @param JSONObj api response JSON object.
     * @throws JsonSyntaxException if API version is incompatible or if JSON structure is invalid.
     */
    private void checkVersion(JsonObject JSONObj) {
        //: Basic structure validation.
        //: Prevents 'NullPointerException' when checking those fields.
        if (!JSONObj.has("metadata")) throw new JsonSyntaxException("Server response JSON has invalid structure - does not have 'metadata' field");
        //^ "features" field has Earthquake instances array.
        //^ "metadata" field has "api" field.
        if (!JSONObj.get("metadata").getAsJsonObject().has("api")) throw new JsonSyntaxException("Server response JSON has invalid structure - does not have 'api' sub-field");
        //^ "api" field has api version (as string).

        if (!JSONObj.get("metadata").getAsJsonObject().get("api").getAsString().startsWith("1.")) throw new JsonSyntaxException("Program is out-of-date for the USGS api.");
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
        return StreamSupport.stream(allInstances.spliterator(), false)
            //^ Uses 'StreamSupport' to address JsonArray's lack of a '.stream()' method.
            .map(JsonElement::getAsJsonObject)
            .filter(instance -> {
                return instance.getAsJsonObject("properties").get("magType").getAsString().startsWith("mw");
                //^ Only want Moment Magnitude type earthquakes.
                //^ '.startsWith' is used as there are multiple Moment Magnitude types such as 'mwc', 'mww', etc.
            })
            .collect(JsonArray::new, JsonArray::add, (a, b) -> b.forEach(a::add));
            //^ Collect all filtered instances into new JSON array and return it.
    }
    /**
     * Validates data structure both of the response JSON object and of its fields.
     * <p>
     * Validation is for structure only - data validity is verified in the record's ('EarthquakeEntry') constructor.
     * @param earthquakeInstance JSON object of the API response.
     * @throws JsonSyntaxException if at least one earthquake instance has invalid structure.
     */
    private void validateEarthquakeInstance(JsonObject earthquakeInstance){
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
        JsonArray geometryEles = geometry.getAsJsonArray("coordinates");
        for (int i = 0; i < 3; i++) {
            if (!geometryEles.get(i).isJsonPrimitive() || !geometryEles.get(i).getAsJsonPrimitive().isNumber()) {
                throw new JsonSyntaxException("At least one of the earthquake instancies, in the response JSON, has incorrect sub-field structure - all 3 'coordinates' elements must be numeric");
            }
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
        Gson gson = new Gson();
        //^ Required instantiation of Gson object to use its deserialization method.
        //^ As local variable, instead of in stream, to avoid repeated instantiation for each element.

        return StreamSupport.stream(earthquakeInstances.spliterator(), false)
            //^ Uses 'StreamSupport' to address JsonArray's lack of a '.stream()' method.
            .map(JsonElement::getAsJsonObject)
            //^ Convert each JSON element into JSON object because 'JsonElement' lacks the methods that 'JsonObject' have.
            .map(instanceObj -> {

                //: Restructure JSON object fields, by making a new JSON obj, to match 'EarthquakeEntry' record fields.
                JsonObject formattedInstanceObj = new JsonObject();
                formattedInstanceObj.add("magnitude", instanceObj.getAsJsonObject("properties").getAsJsonPrimitive("mag"));
                formattedInstanceObj.add("place", instanceObj.getAsJsonObject("properties").getAsJsonPrimitive("place"));
                formattedInstanceObj.add("time", instanceObj.getAsJsonObject("properties").getAsJsonPrimitive("time"));
                formattedInstanceObj.add("longitude", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsJsonPrimitive());
                formattedInstanceObj.add("latitude", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsJsonPrimitive());
                formattedInstanceObj.add("depth", instanceObj.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(2).getAsJsonPrimitive());

                return gson.fromJson(formattedInstanceObj, EarthquakeEntry.class);
                //^ Convert (deserialize) JSON object into 'EarthquakeEntry' record.
            })
            .toArray(EarthquakeEntry[]::new);
            //^ Collect all mapped 'EarthquakeEntry' records into an array and return it.
    }
}
