import java.time.LocalDateTime;

public record EarthquakeEntry(
    double magnitude,
    String place,
    LocalDateTime time,
    double longitude,
    double latitude,
    double depth
) {
    public void EarthquakeEntry(){

    }
    public static void validateMagnitude(double magnitude) {

    }
    public static void validatePlace(String place) {

    }
    public static void validateTime(LocalDateTime time) {

    }
    public static void validateLongitude(double longitude) {

    }
    public static void validateLatitude(double latitude) {

    }
    public static void validateDepth(double depth) {

    }
}
