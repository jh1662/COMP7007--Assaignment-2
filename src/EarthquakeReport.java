import java.time.LocalDateTime;

public record EarthquakeReport(
    EarthquakeEntry[] dataSet,
    //^ Raw earthquake data.
    double[] magnitudeQuartiles, double standardDeviationMagnitude, double meanMagnitude,
    //^ Earthquake strength.
    double meanIntermissionTime, double monthlyFrequency,
    //^ Earthquake timing.
    double[] centroidCoords,
    //^ Earthquake location.
    double predictedNextMagnitude, LocalDateTime predictedNextTime, double[] predictedNextCoordinates
    //^ Predictions.
) {
    /// //^ Because we are encouraged to use lambda expressions and functional programming, in a compact and effective manner, we will implement methods to read API data from class field instead of method parameter.
    public void EarthquakeReport(EarthquakeEntry[] dataSet) {


        /*
        this(
            data,

            EarthquakeReport.findQuartilesMagnitude(),
            EarthquakeReport.findStandardDeviationMagnitude(),
            EarthquakeReport.findMeanMagnitude(),

            EarthquakeReport.findMeanIntermissionTime(),
            EarthquakeReport.findMonthlyFrequency(),

            EarthquakeReport.findCentroidCoords(),

            EarthquakeReport.predictNextMagnitude(),
            EarthquakeReport.predictNextTime(),
            EarthquakeReport.predictNextCoordinates()
        );
        */

    }

    //! Because helper most helper methods are called before record's instantiation, they must be static.

    //: Averages
    private static double findMeanMagnitude(){
        return 0.0;
    }
    private static double findStandardDeviationMagnitude(){
        return 0.0;
    }
    private static double[] findQuartilesMagnitude(){
        //* includes Q1, Q2 (median), and Q3
        return new double[]{0.0,0.0,0.0};
    }
    private static double[] findCentroidCoords(){
        //* includes latitude and longitude
        return new double[]{0.0,0.0};
    }
    private static double findMeanIntermissionTime(){
        return 0.0;
    }
    private static double findMonthlyFrequency(){
        return 0.0;
    }

    //: Predictions based on historical data and averages
    private static double predictNextMagnitude(){
        return 0.0;
    }
    private static double[] predictNextCoordinates(){
        //* includes latitude and longitude
        return new double[]{0.0,0.0};
    }
    private static LocalDateTime predictNextTime(){
        return LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "";
    }
}
