public enum EarthquakeStats {
    //: Enum constants names based on names from the API JSON response fields.
    MAG("magnitude"),
    //^ Magnitude is decimal.
    PLACE("place"),
    //^ Place is human-readable string.
    TIME("date and time"),
    //^ Time is LocalDateTime.
    //: To reduce complexity, 'COORDINATES' field is split up into longitude, latitude, and depth fields (instead of another enum dict).
    LONGITUDE("longitude"),
    //^ Longitude is decimal (-180 to 180).
    LATITUDE("latitude"),
    //^ Longitude is decimal (-90 to 90).
    DEPTH("depth");
    //^ Depth is decimal (in kilometers).

    private final String value;
    //^ Holds string representation of the query params.

    /**
     * Constructor assigns string representation of the earthquake stat names.
     */
    EarthquakeStats(String value) { this.value = value; }

    /**
     * Overrides default 'toString' method to serve as a getter method for 'this.value'.
     * @return String value of the earthquake stat name.
     */
    @Override
    public String toString() { return this.value; }
}
