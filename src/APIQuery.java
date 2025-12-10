import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Record class representing an API query to the USGS earthquake data API.
 * Immutable data structure holding all query parameters needed to construct a valid API request URL.
 * @param limit
 * @param latitude
 * @param longitude
 * @param radiusKm
 * @param startTime
 * @param endTime
 * @param lowestMagnitude
 * @param highestMagnitude
 * @param lowestDepth
 * @param highestDepth
 */
public record APIQuery(int limit,
   double latitude,
   double longitude,
   int radiusKm,
   ZonedDateTime startTime,
   ZonedDateTime endTime,
   double lowestMagnitude,
   double highestMagnitude,
   double lowestDepth,
   double highestDepth
) {
    private final static String format = "format=geojson&jsonerror&";
    //^ Want API to always return data in JSON format with JSON error messages instead of alternatives.
    //^ Kept as a separate field (instead of combined with 'baseURL') for clarity’s sake.
    private final static String baseURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?" + APIQuery.format;
    //^ Base URL for all API data requests.

    /**
     * Canonical constructor for APIQuery record.
     * Validates all args before assignment to fields (using static private methods).
     * <p>
     * Params of this canonical constructor are implicitly defined as the record fields -
     * thus see Java Record documentation for the params.
     * @throws IllegalArgumentException if any arg is invalid.
     */
    public APIQuery  {
        APIQuery.validateLimit(limit);
        APIQuery.validateLatitude(latitude);
        APIQuery.validateLongitude(longitude);
        APIQuery.radius(radiusKm);
        APIQuery.validateStartTimestamp(startTime);
        APIQuery.validateEndTimestamp(endTime, startTime);
        APIQuery.validateLowestMagnitude(lowestMagnitude);
        APIQuery.validateHighestMagnitude(highestMagnitude, lowestMagnitude);
        APIQuery.validateLowestDepth(lowestDepth);
        APIQuery.validateHighestDepth(highestDepth, lowestDepth);
    }

    //: Helper methods for validation assistance of common query args.
    /**
     * Helper validation method.
     * Ensure it is neither in the future nor before 1900AD.
     * @param timestamp The timestamp (in UTC), as ZonedDateTime representation, to validate.
     * @return true if timestamp is valid, false otherwise.
     */
    private static boolean timestampIsValid(ZonedDateTime timestamp) {
        return !(timestamp.isAfter(ZonedDateTime.now()) ||
            timestamp.isBefore(ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }
    /**
     * Helper validation method.
     * Ensure magnitude is within valid bounds (-5.0 to 10.0).
     * <p>
     * Validation bounds are based on historical earthquake data records; further explained in 'EarthquakeEntry.validateMagnitude' record method.
     * @param magnitude The magnitude, as double representation, to validate.
     * @return true if magnitude is valid, false otherwise.
     */
    private static boolean magnitudeIsValid(double magnitude) {
        return (magnitude < -5.0 || magnitude > 10.0);
    }
    /**
     * Helper validation method.
     * Ensure depth is within valid bounds (0 to 800 kilometers).
     * <p>
     * Validation bounds are based on historical earthquake data records; further explained in 'EarthquakeEntry.validateMagnitude' record method.
     * @param depth The depth, as double representation, to validate.
     * @return true if depth is valid, false otherwise.
     */
    private static boolean depthIsValid(double depth) {
        return (depth < 0 || depth > 800);
    }

    //: Helper validator methods for individual query params.
    /**
     * Validates arg for latitude field.
     * Ensures it is within valid bounds (-90 to 90).
     * @param latitude The latitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'latitude' is out of bounds.
     */
    private static void validateLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException(latitude + " is invalid. Latitude must be between -90 and 90 degrees.");
    }
    /**
     * Validates arg for longitude field.
     * Ensures it is within valid bounds (-180 to 180).
     * @param longitude The longitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'longitude' is out of bounds.
     */
    private static void validateLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException(longitude + " is invalid. Longitude must be between -180 and 180 degrees.");
    }
    /**
     * Validates arg for radius field.
     * Ensures it is neither negative nor non-existent, and does not exceed half the Earth's circumference.
     * @param km The radius, in kilometers, as int representation, to validate.
     * @throws IllegalArgumentException if 'radius' is negative, non-existent (0), or exceeds half the Earth's circumference.
     */
    private static void radius(int km) {
        if (km <= 0) throw new IllegalArgumentException(km + " is invalid. Radius cannot be negative nor non-existent.");
        if (km > 20001.6) throw new IllegalArgumentException(km + " is invalid. Earth circumference is ~40,000 km, thus radius cannot exceed 20,001.6 km.");
        //^ Was going to limit radius is half the Earth's circumference (20,038Km) but USGS api doc uses 20,001.6 km as the default radius;
        //^ thus 20,001.6 km is the assumed upper limit for radius.
        //^ Source - https://earthquake.usgs.gov/fdsnws/event/1/ .
    }
    /**
     * Validates arg for start timestamp field.
     * Ensures it is neither in the future nor before 1900AD.
     * Calls helper method 'timestampIsValid' for actual validation logic.
     * @param start The start timestamp (in UTC), as ZonedDateTime representation, to validate.
     * @throws IllegalArgumentException if 'start' is in the future (after current time and date) or before 1900AD.
     */
    private static void validateStartTimestamp(ZonedDateTime start) {
        if (APIQuery.timestampIsValid(start)) throw new IllegalArgumentException(start + " is invalid. Start time cannot be in the future nor before 1900AD.");
    }
    /**
     * Validates arg for end timestamp field.
     * Ensures it is neither in the future nor before 1900AD, and not before start time.
     * Calls helper method 'timestampIsValid' for common timestamp validation logic.
     * @param end The end timestamp (in UTC), as ZonedDateTime representation, to validate.
     * @param start The start timestamp (in UTC), as ZonedDateTime representation, to compare against.
     * @throws IllegalArgumentException if 'end' is in the future (after current time and date), before 1900AD, or before 'start' time.
     */
    private static void validateEndTimestamp(ZonedDateTime end, ZonedDateTime start) {
        if (APIQuery.timestampIsValid(start)) throw new IllegalArgumentException(end + " is invalid. End time cannot be in the future nor before 1900AD.");
        if (end.isBefore(start)) throw new IllegalArgumentException(end + " is invalid. End time cannot be before start time.");
    }
    /**
     * Validates arg for lowest magnitude field.
     * Ensures it is within valid bounds (-5.0 to 10.0).
     * Calls helper method 'magnitudeIsValid' for actual validation logic.
     * @param lowestMagnitude The lowest magnitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'lowestMagnitude' is out of bounds.
     */
    private static void validateLowestMagnitude(double lowestMagnitude) {
        if (APIQuery.magnitudeIsValid(lowestMagnitude)) throw new IllegalArgumentException(lowestMagnitude + " is invalid. Lowest magnitude cannot be more than 10.0 nor less than -5.0.");
    }
    /**
     * Validates arg for highest magnitude field.
     * Ensures it is within valid bounds (-5.0 to 10.0), and not less than the lowest magnitude arg.
     * Calls helper method 'magnitudeIsValid' for common magnitude validation logic.
     * @param highestMagnitude The highest magnitude, as double representation, to validate.
     * @param lowestMagnitude The lowest magnitude, as double representation, to compare against.
     * @throws IllegalArgumentException if 'highestMagnitude' is out of bounds or less than 'lowestMagnitude'.
     */
    private static void validateHighestMagnitude(double highestMagnitude, double lowestMagnitude) {
        if (APIQuery.magnitudeIsValid(lowestMagnitude)) throw new IllegalArgumentException(highestMagnitude + " is invalid. Highest magnitude cannot be more than 10.0 nor less than -5.0.");
        if (highestMagnitude < lowestMagnitude) throw new IllegalArgumentException(highestMagnitude + " is invalid. Highest magnitude cannot be less than lowest magnitude.");
    }
    /** Validates arg for lowest depth field.
     * Ensures it is within valid bounds (0 to 800 kilometers).
     * Calls helper method 'depthIsValid' for actual validation logic.
     * @param lowestDepth The lowest depth, as double representation, to validate.
     * @throws IllegalArgumentException if 'lowestDepth' is out of bounds.
     */
    private static void validateLowestDepth(double lowestDepth) {
        if (APIQuery.depthIsValid(lowestDepth)) throw new IllegalArgumentException(lowestDepth + " is invalid. Lowest depth must be above 0 and below 800 km.");
    }
    /** Validates arg for highest depth field.
     * Ensures it is within valid bounds (0 to 800 kilometers), and not less than the lowest depth arg.
     * Calls helper method 'depthIsValid' for common depth validation logic.
     * @param highestDepth The highest depth, as double representation, to validate.
     * @param lowestDepth The lowest depth, as double representation, to compare against.
     * @throws IllegalArgumentException if 'highestDepth' is out of bounds or less than 'lowestDepth'.
     */
    private static void validateHighestDepth(double highestDepth, double lowestDepth) {
        if (APIQuery.depthIsValid(highestDepth)) throw new IllegalArgumentException(highestDepth + " is invalid. Lowest depth must be above 0 and below 800 km.");
        if (highestDepth < lowestDepth) throw new IllegalArgumentException(highestDepth + " is invalid. Highest depth cannot be less than lowest depth.");
    }
    /**
     * Validates arg for limit field.
     * Ensures it is neither negative nor non-existent, and does not exceed 20,000 entries as dictated by USGS api.
     * @param maxEntries The maximum number of entries to retrieve, as int representation, to validate.
     * @throws IllegalArgumentException if 'maxEntries' is negative, non-existent (0), or exceeds 20,000 entries.
     */
    private static void validateLimit(int maxEntries) {
        if (maxEntries <= 0) throw new IllegalArgumentException(maxEntries + " is invalid. Limit cannot be negative nor non-existent.");
        if (maxEntries > 20000) throw new IllegalArgumentException(maxEntries + " is invalid. Limit cannot exceed 20,000 entries as USGS dictated.");
    }

    /**
     * Overrides default 'toString' method.
     * Renders URL api query by combining bas URL, query params, and query args (record components).
     * @return String representation of the API query as a URL.
     */
    @Override
    public String toString() {
        return APIQuery.baseURL +
            QueryParam.LIMIT.toString() + this.limit + "&" +
            QueryParam.LATITUDE.toString() + this.latitude + "&" +
            QueryParam.LONGITUDE.toString() + this.longitude + "&" +
            QueryParam.RADIUS_KM.toString() + this.radiusKm + "&" +
            QueryParam.START_TIME.toString() + this.startTime.toInstant().toString() + "&" +
            QueryParam.END_TIME.toString() + this.endTime.toInstant().toString() + "&" +
            QueryParam.LOWEST_MAGNITUDE.toString() + this.lowestMagnitude + "&" +
            QueryParam.HIGHEST_MAGNITUDE.toString() + this.highestMagnitude + "&" +
            QueryParam.LOWEST_DEPTH.toString() + this.lowestDepth + "&" +
            QueryParam.HIGHEST_DEPTH.toString() + this.highestDepth;
    }
}
