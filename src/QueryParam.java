enum QueryParam {
    FORMAT("format="),
    //^ Param has constant arg "geojson&jsonerror".
    LIMIT("limit="),
    //^ Max number of records to retrieve.
    //^ Valid as Positive int up to 20000.
    LATITUDE("latitude="),
    LONGITUDE("longitude="),
    RADIUS_KM("maxradiuskm="),
    START_TIME("starttime="),
    //^ Value expected in "ISO8601" and UTC format.
    //^ Valid as from current date back to 30 days before the end time.
    END_TIME("endtime="),
    //^ Same format as 'START_TIME'.
    //^ Must not exceed current data and time.
    LOWEST_MAGNITUDE("minmagnitude="),
    HIGHEST_MAGNITUDE("maxmagnitude="),
    LOWEST_DEPTH("mindepth="),
    HIGHEST_DEPTH("maxdepth=");

    private final String value;
    //^ Holds string representation of the query params.

    /**
     * Constructor assigns string representation of the query params.
     */
    QueryParam(String value) { this.value = value; }

    /**
     * Overrides default 'toString' method to serve as a getter method for 'this.value'.
     * @return String value of the query params for the URL request.
     */
    @Override
    public String toString() { return this.value; }
}
