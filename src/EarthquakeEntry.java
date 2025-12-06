import java.time.ZoneId;
import java.time.ZonedDateTime;

public record EarthquakeEntry(
    double magnitude,
    String place,
    ZonedDateTime time,
    double longitude,
    double latitude,
    double depth
) {
    public EarthquakeEntry{
        EarthquakeEntry.validateMagnitude(magnitude);
        EarthquakeEntry.validatePlace(place);
        EarthquakeEntry.validateTime(time);
        EarthquakeEntry.validateLongitude(longitude);
        EarthquakeEntry.validateLatitude(latitude);
        EarthquakeEntry.validateDepth(depth);
    }

    private static void validateMagnitude(double magnitude) {
        if (magnitude > 10.0) throw new IllegalArgumentException(magnitude + " is invalid. Magnitude cannot be more than 10.0.");
        //^ Highest recorded magnitude is 9.5 (1960 Valdivia earthquake) - thus it is safe to assume anything above 10.0 is invalid.
        //^ Source - https://en.wikipedia.org/wiki/Moment_magnitude_scale .
        if (magnitude < -5.0) throw new IllegalArgumentException(magnitude + " is invalid. Magnitude cannot be less than -5.0.");
        //^ Magnitudes can go negative.
        //^ Lowest recorded magnitude is more vague, sources say between -2.0 or -3.0 - thus -5 is a safe lower bound.
    }
    private static void validatePlace(String place) {
        if (place == null || place.isBlank()) throw new IllegalArgumentException("Place cannot be null or blank.");
        //^ '.isBlank()' includes whitespace check.
    }
    private static void validateTime(ZonedDateTime time) {
        if (time.isAfter(ZonedDateTime.now())) throw new IllegalArgumentException(" is invalid. Entry time cannot be in the future.");
        if (time.isBefore(ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))) {
            throw new IllegalArgumentException("Entry time cannot before 1900AD.");
        }
    }
    private static void validateLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees.");
    }
    private static void validateLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees.");
    }
    private static void validateDepth(double depth) {
        if (depth < 0) throw new IllegalArgumentException("Depth cannot be negative.");
        if (depth > 800) throw new IllegalArgumentException("Depth must not be deeper than 800 kilometers.");
        //^ Deepest recorded earthquake depth is ~735.8 km - thus it is safe to assume anything deeper than 800 is invalid.
        //^ Source - https://en.wikipedia.org/wiki/Deep-focus_earthquake .
    }

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
