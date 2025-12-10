import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Record class representing a single earthquake entry.
 * Immutable data structure holding details of an earthquake instance.
 * @param magnitude The magnitude of the earthquake.
 * @param place The place where the earthquake occurred.
 * @param time The time when the earthquake occurred.
 * @param longitude The longitude of the earthquake's epicenter.
 * @param latitude The latitude of the earthquake's epicenter.
 * @param depth The depth of the earthquake's epicenter.
 */
public record EarthquakeEntry(
    double magnitude,
    String place,
    ZonedDateTime time,
    double longitude,
    double latitude,
    double depth
) {
    /**
     * Canonical constructor for EarthquakeEntry record.
     * Validates all args before assignment to fields (using static private methods).
     * <p>
     * Params of this canonical constructor are implicitly defined as the record fields -
     * thus see Java Record documentation for the params.
     * @throws IllegalArgumentException if any arg is invalid.
     */
    public EarthquakeEntry{
        EarthquakeEntry.validateMagnitude(magnitude);
        EarthquakeEntry.validatePlace(place);
        EarthquakeEntry.validateTime(time);
        EarthquakeEntry.validateLongitude(longitude);
        EarthquakeEntry.validateLatitude(latitude);
        EarthquakeEntry.validateDepth(depth);
    }
    /**
     * Validates arg for 'this.magnitude' field.
     * Ensures it is within valid bounds (-5.0 to 10.0).
     * <p>
     * Due to the lack of an absolute limits for earthquake magnitudes (both upper and lower),
     * the limits are quite significant - more than the highest and lowest recorded magnitudes.
     * @param magnitude The magnitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'magnitude' is out of bounds.
     */
    private static void validateMagnitude(double magnitude) {
        if (magnitude > 10.0) throw new IllegalArgumentException(magnitude + " is invalid. Magnitude cannot be more than 10.0.");
        //^ Highest recorded magnitude is 9.5 (1960 Valdivia earthquake) - thus it is safe to assume anything above 10.0 is invalid.
        //^ Source - https://en.wikipedia.org/wiki/Moment_magnitude_scale .
        if (magnitude < -5.0) throw new IllegalArgumentException(magnitude + " is invalid. Magnitude cannot be less than -5.0.");
        //^ Magnitudes can go negative.
        //^ Lowest recorded magnitude is more vague, sources say between -2.0 or -3.0 - thus -5 is a safe lower bound.
    }
    /**
     * Validates arg for 'this.place' field.
     * Ensures it is neither null nor blank.
     * @param place The place, as string representation, to validate.
     * @throws IllegalArgumentException if 'place' is null or blank.
     */
    private static void validatePlace(String place) {
        if (place == null || place.isBlank()) throw new IllegalArgumentException(place + " is invalid. Place cannot be null or blank.");
        //^ '.isBlank()' includes whitespace check.
    }
    /**
     * Validates arg for 'this.time' field.
     * Ensures it is neither in the future nor before 1900AD.
     * @param time The timestamp, as ZonedDateTime representation, to validate.
     * @throws IllegalArgumentException if 'time' is in the future or before 1900AD.
     */
    private static void validateTime(ZonedDateTime time) {
        if (time.isAfter(ZonedDateTime.now())) throw new IllegalArgumentException(time + " is invalid. Entry time cannot be in the future.");
        if (time.isBefore(ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))) {
            throw new IllegalArgumentException("Entry time cannot before 1900AD.");
        }
    }
    /**
     * Validates arg for 'this.longitude' field.
     * Ensures it is within valid bounds (-180 to 180).
     * @param longitude The longitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'longitude' is out of bounds.
     */
    private static void validateLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException(longitude + " is invalid. Longitude must be between -180 and 180 degrees.");
    }
    /**
     * Validates arg for 'this.latitude' field.
     * Ensures it is within valid bounds (-90 to 90).
     * @param latitude The latitude, as double representation, to validate.
     * @throws IllegalArgumentException if 'latitude' is out of bounds.
     */
    private static void validateLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException(latitude + " is invalid. Latitude must be between -90 and 90 degrees.");
    }
    /**
     * Validates arg for 'this.depth' field.
     * Ensures it is within valid bounds (0 to 800 kilometers).
     * @param depth The depth, as double representation, to validate.
     * @throws IllegalArgumentException if 'depth' is out of bounds.
     */
    private static void validateDepth(double depth) {
        if (depth <= 0) throw new IllegalArgumentException(depth + " is invalid. Depth cannot be negative nor zero.");
        //^ USGS API allows depths to be between -100 and 1000 km ( https://earthquake.usgs.gov/fdsnws/event/1/ );
        //^ However negatives depths are margins or errors and depths of 0 km means unable to calculate ( https://www.usgs.gov/faqs/what-does-it-mean-earthquake-occurred-a-depth-0-km-how-can-earthquake-have-a-negative-depth ).
        if (depth > 800) throw new IllegalArgumentException(" is invalid. Depth must not be deeper than 800 kilometers.");
        //^ Deepest recorded earthquake depth is ~735.8 km - thus it is safe to assume anything deeper than 800 is invalid.
        //^ Source - https://en.wikipedia.org/wiki/Deep-focus_earthquake .
    }
    /**
     * Overrides default 'toString' method.
     * Provide a human-readable text representation of the earthquake entry.
     * @return String representation of the earthquake entry.
     */
    @Override
    public String toString() {
        return String.format(
            //^ Using string format for better readability.
            "earthquake entry: earthquake in %s (at %.3f\u00B0 N %.3f\u00B0 E) happened at %s with a magnitude of %.1fMw hitting a depth: %.2f km)]",
            //^ String template with placeholders.
            //^ Using '\u00B0' unicode for degree symbol.
            this.place, this.latitude, this.longitude, this.time, this.magnitude, this.depth
            //^ Data for said placeholders.
            //^ 'this.time.toString()' is implicitly called by the '%s' placeholder.
        );
    }
}
