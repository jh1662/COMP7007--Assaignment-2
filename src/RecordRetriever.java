import java.time.LocalDateTime;

public class RecordRetriever {
    //! API doc at - https://earthquake.usgs.gov/fdsnws/event/1/

    //: Singleton pattern implementation.
    static final private RecordRetriever instance = new RecordRetriever();
    static public RecordRetriever getInstance() { return RecordRetriever.instance; }

    //: Constant URL args.
    private final String formatArg = "geojson&jsonerror";
    //^ Want API to always return data in JSON format with JSON error messages instead of alternatives.
    //^ Kept as a field (instead of combined with 'this.baseURL') for clarity.
    /// private final int limitArgUpperLimit = 20000;
    /// //^ The upper limit for the 'limit' param (hence 2 "limit"s in the field name).

    private final String baseURL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    //^ Base URL for all API data requests.

    private final QueryValidator queryValidator = new QueryValidator();
    //^ Validation responsibility delegated to 'QueryValidator' leaf class.

    //! No constructor to enforce singleton pattern.

    public EarthquakeEntry[] requestAPIDataRecord(int limit, double latitude, double longitude, int radiusKM, LocalDateTime startTimeDate, LocalDateTime endTimeDate, double lowestMagnitude, double highestMagnitude) {
        //* KM radius is a very precise unit but earthquakes are not precise, thus integer are sufficient.
        return new EarthquakeEntry[]{};
    }

    //: Helper methods for 'requestAPIDataRecord' method.
    private boolean isQueryValid(int limit, double latitude, double longitude, int radiusKM, LocalDateTime startTimeDate, LocalDateTime endTimeDate, double lowestMagnitude, double highestMagnitude) {
        return true;
    }
    private boolean checkVersion() {
        return true;
    }
    private Object toObject(String jsonData) {
        return null;
    }
    private EarthquakeEntry[] toRecord(Object data) {
        return new EarthquakeEntry[]{};
    }
    private String buildQueryURL() {
        return "";
    }
    private String HTTPRequest(String queryURL) {
        return "";
    }
}
