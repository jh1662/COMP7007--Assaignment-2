import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.IntStream;

public record EarthquakeReport(
    EarthquakeEntry[] dataSet,
    //^ Raw earthquake data.
    double[] magnitudeQuartiles,/* double standardDeviationMagnitude, */ double meanMagnitude,
    //^ Earthquake strength in Mw.
    double meanDepth,
    //^ Earthquake depth in Km.
    double meanIntermissionTimeHours, double monthlyFrequency,
    //^ Earthquake timing.
    double[] centroidCoords,
    //^ Earthquake location.
    /*double predictedNextMagnitude, double[] predictedNextCoordinates,*/ ZonedDateTime[] predictedNextTime
    //^ Predictions.
) {

    public static EarthquakeReport of(EarthquakeEntry[] dataSet, ZonedDateTime startTime, ZonedDateTime endTime) {
        //^ Canonical constructor forbids logic before 'this(' call, thus static factory method is used instead.

        if (dataSet.length == 0) { throw new IllegalArgumentException("fetches Earthquake data set is empty."); }

        //: Local processing of the data set.
        double meanMagnitude = EarthquakeReport.findMeanMagnitude(dataSet);
        double[] magnitudeQuartiles = EarthquakeReport.findQuartilesMagnitude(dataSet);
        double meanDepth = EarthquakeReport.findMeanDepth(dataSet);
        double[] centroidCoords = EarthquakeReport.findCentroidCoords(dataSet);
        double meanIntermissionTimeHours = EarthquakeReport.findMeanIntermissionDurationHours(dataSet);


        //: Also local processing (of the data set) but requires prior local processing results as args.
        double monthlyFrequency = EarthquakeReport.findMonthlyFrequency(dataSet, startTime,endTime);
        ZonedDateTime[] nextTimeRange = EarthquakeReport.predictNextTime(dataSet, monthlyFrequency, meanIntermissionTimeHours);

        return new EarthquakeReport(
            dataSet,
            magnitudeQuartiles, /*standardDeviationMagnitude,*/ meanMagnitude,
            meanDepth,
            meanIntermissionTimeHours, monthlyFrequency,
            centroidCoords,
            /*predictedNextMagnitude,*/ /*predictedNextCoordinates,*/ nextTimeRange
        );
    }

    //! Because helper most helper methods are called before record's instantiation, they must be static.

    //: Averages
    private static double findMeanMagnitude(EarthquakeEntry[] dataSet){
        if (dataSet.length == 1) return dataSet[0].magnitude();
        //^ Mean of one value is itself - shortcut to avoid stream overhead.
        return Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .mapToDouble(EarthquakeEntry::magnitude)
            //^ Maps the magnitude component to turn record array stream into a double stream.
            //^ 'EarthquakeEntry::magnitude' lambda is short for 'entry -> entry.magnitude()'.
            .average()
            //^ Find mean from subjected stream.
            .getAsDouble();
            //^ Assure result is an existing decimal - converting 'OptionalDouble' to 'double'.
    }
    /*
    private static double findStandardDeviationMagnitude(){
        return 0.0;
    }
    */
    private static double[] findQuartilesMagnitude(EarthquakeEntry[] dataSet){
        //* includes Q1, Q2 (median), and Q3

        //: preventing unexpected behaviours (minimising logic errors)
        switch (dataSet.length) {
            //* Using "enhanced switch" syntax for clarity.
            case 1 -> {
                //* One records means all quartiles are the same.
                double singleMagnitude = dataSet[0].magnitude();
                return new double[]{singleMagnitude, singleMagnitude, singleMagnitude};
            }
            case 2 -> {
                //* Two records means Q1 and Q3 are the two magnitudes, Q2 is their mean.
                double mag1 = dataSet[0].magnitude();
                double mag2 = dataSet[1].magnitude();
                double q1 = Math.min(mag1, mag2);
                double q3 = Math.max(mag1, mag2);
                double q2 = (mag1 + mag2) / 2.0;

                return new double[]{q1, q2, q3};
            }
        }

        //: Common data preparation for quartile calculation
        double[] magnitudes = Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .mapToDouble(EarthquakeEntry::magnitude)
            //^ Maps record array stream into a double stream of magnitudes.
            .sorted()
            //^ Sort the double stream in ascending order.
            .toArray();
            //^ Convert stream back into array to access.
        int n = magnitudes.length;

        return new double[]{
            //* Using logic in double[] declaration using streams.
            //! There must be a way to integrate lambdas here...
            // Finding lower quantile (Q1).
            (n/2 % 2 == 0)
                //^ If-statement to see if lower half has an even number of elements.
                ? Arrays.stream(new double[]{magnitudes[n/4 - 1], magnitudes[n/4]}).average().getAsDouble()
                //^ Executes if even.
                //^ "new double[]" - creates a temporary array to hold the two middle values to subject as stream.
                //^ ".average()" - finding mean as Q1 is between those two values.
                //^ ".getAsDouble()" - converts 'OptionalDouble' to 'double' as we are sure Q1 exists.
                : magnitudes[n/4],
                //^ Executes if odd.

            // Finding median (Q2).
            (n % 2 == 0)
                //^ If-statement to see if total number of elements is even.
                ? Arrays.stream(new double[]{magnitudes[n/2 - 1], magnitudes[n/2]}).average().getAsDouble()
                //^ Executes if even.
                : magnitudes[n/2],
                //^ Executes if odd.

            // Finding upper quantile (Q3).
            (n/2 % 2 == 0)
                //^ If-statement to see if upper half has an even number of elements.
                ? Arrays.stream(new double[]{magnitudes[(3*n)/4 - 1], magnitudes[(3*n)/4]}).average().getAsDouble()
                //^ Executes if even.
                : magnitudes[(3*n)/4]
                //^ Executes if odd.
        };

    }
    private static double[] findCentroidCoords(EarthquakeEntry[] dataSet){
        //* includes latitude and longitude
        if (dataSet.length == 1) return new double[]{dataSet[0].latitude(), dataSet[0].longitude()};

        return new double[]{
            //* Each element calculation is done the same way to the stream used in 'this.findMeanMagnitude' method.
            Arrays.stream(dataSet)
                //* Finding mean latitude.
                .mapToDouble(EarthquakeEntry::latitude)
                .average()
                .getAsDouble()
            ,
            Arrays.stream(dataSet)
                //* Finding mean longitude.
                .mapToDouble(EarthquakeEntry::longitude)
                .average()
                .getAsDouble()
        };
    }
    private static double findMeanIntermissionDurationHours(EarthquakeEntry[] dataSet){
        //* User wouldn't realistically care about minutes or seconds in intermission time.
        if (dataSet.length == 1) { return 0; }
        //^ Cannot have intermission time with only one record.
        //^ Simply return 0 instead of error because lack of intermission is fully expected behaviour.

        //: Sorting times in ascending order is critical to prevent negative durations when calculating mean.
        ZonedDateTime sortedTimes[] = Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .map(EarthquakeEntry::time)
            //^ Maps the time component to turn record array stream into a 'ZonedDateTime' stream.
            //^ 'EarthquakeEntry::time' lambda is short for 'entry -> entry.time()'.
            .sorted()
            //^ Sort the 'ZonedDateTime' stream in ascending order.
            .toArray(ZonedDateTime[]::new);
            //^ Convert stream back into array to access.

        return IntStream.range(0, sortedTimes.length - 1)
            //^ Turns sorted date/time array into an integer stream.
            //^ Using integer stream (instead of '.stream') to access array indices.
            .mapToLong(i -> Duration.between(sortedTimes[i], sortedTimes[i + 1]).toHours())
            //^ Iterates through each index (of the stream) to calculate duration (in hours) between each consecutive date/time.
            //^ Returns the results of iteration as a new long stream.
            .average()
            //^ Find mean from subjected stream.
            .orElseThrow();
            //^ Assure result is an existing decimal - converting 'OptionalDouble' to 'double'.
    }
    private static double findMeanDepth(EarthquakeEntry[] dataSet){
        if (dataSet.length == 1) return dataSet[0].depth();
        //^ Mean of one value is itself - shortcut to avoid stream overhead.
        return Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .mapToDouble(EarthquakeEntry::depth)
            //^ Maps the depth component to turn record array stream into a double stream.
            //^ 'EarthquakeEntry::depth' lambda is short for 'entry -> entry.depth()'.
            .average()
            //^ Find mean from subjected stream.
            .getAsDouble();
            //^ Assure result is an existing decimal - converting 'OptionalDouble' to 'double'.
    }
    private static double findMonthlyFrequency(EarthquakeEntry[] dataSet, ZonedDateTime startTime, ZonedDateTime endTime){
        int occurrences = dataSet.length;
        int durationDays = (int) Duration.between(startTime, endTime).toDays();

        return (double) durationDays / occurrences;
        //^ "(double)" makes sure decimal division is used instead of integer division.
    }

    //: Predictions based on historical data and averages
    //! Predicted next magnitude is just the mean.
    //! Predicted next location is just the centroid coordinates.
    /*
    private static double predictNextMagnitude(EarthquakeEntry[] dataSet){
        return Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .mapToDouble(EarthquakeEntry::magnitude)
            //^ Maps the magnitude component to turn record array stream into a double stream.
            //^ 'EarthquakeEntry::magnitude' is shorthand for 'entry -> entry.magnitude()'.
            .max()
            //^ Find the maximum value from the stream.
            .orElseThrow();
            //^ Assure result is an existing decimal - converting 'OptionalDouble' to 'double'.
            //^ An exception is thrown when no maximum exists (i.e. empty stream) but that will not happen.
    }
    */
    /*
    private static double[] predictNextCoordinates(EarthquakeEntry[] dataSet, double[] centroidCoords){
        //* includes latitude and longitude
        double
        return new double[]{0.0,0.0};
    }
    */
    private static ZonedDateTime[] predictNextTime(EarthquakeEntry[] dataSet, double monthlyFrequency, double meanIntermissionDurationHours){
        //* Time is a much harder prediction to make, due to its nature, thus a prediction range is calculated instead.
        //* As mentioned in 'this.findMeanIntermissionDurationHours', time prediction uses hours.
        ZonedDateTime latestOccurrence = Arrays.stream(dataSet)
            //^ Turn subjected array into stream.
            .map(EarthquakeEntry::time)
            //^ Maps the time component to turn record array stream into a 'ZonedDateTime' stream.
            //^ 'EarthquakeEntry::time' lambda is short for 'entry -> entry.time()'.
            .max(ZonedDateTime::compareTo)
            //^ Find the latest date/time from the stream using '.compareTo' as the comparator.
            //^ The comparator arg is needed as 'ZonedDateTime' instances are not comparable by default.
            //^ Something to do with not implementing 'Comparable' interface.
            //^ 'ZonedDateTime::compareTo' is short for '(time1, time2) -> time1.compareTo(time2)'.
            .orElseThrow();
            //^ Assure result is an existing date/time - converting 'Optional' to 'ZonedDateTime'.

        ZonedDateTime prediction1 = latestOccurrence.plusHours((long) meanIntermissionDurationHours);
        ZonedDateTime prediction2 = latestOccurrence.plusHours((long) monthlyFrequency*24);

        //: Making sure to return prediction range in chronological order.
        if (prediction1.isBefore(prediction2)) return new ZonedDateTime[]{ prediction1, prediction2 };
        return new ZonedDateTime[]{ prediction2, prediction1 };
    }

    //: String representation rendering methods
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder("Earthquake report:\n");
        //^ In this method, StringBuilder is a better alternative for string concatenation in terms of code readability.

        report.append(String.format("> Magnitude - mean of %.1f with quartiles (Q1, Q2, and Q3) of %.1f, %.1f, and %.1f.\n",
            this.meanMagnitude, this.magnitudeQuartiles[0], this.magnitudeQuartiles[1], this.magnitudeQuartiles[2]
        ));
        report.append(String.format("> Timing - mean intermission time of %.1f hours with a monthly frequency of %.2f days.\n",
            this.meanIntermissionTimeHours, this.monthlyFrequency
        ));
        report.append(String.format("> Location - centroid at (%.3f\u00B0 N, %.3f\u00B0 E).\n",
            this.centroidCoords[0], this.centroidCoords[1]
        ));
        report.append(String.format("> Depth - mean of %.2f km.\n",
            this.meanDepth
        ));
        report.append(String.format("> Predicted next earthquake occurrence (based on given data set) - estimated to occur at %.3f\u00B0 N, %.3f\u00B0 E, with magnitude strength of %.1f hitting %.2f deep, between %s and %s.\n",
            this.centroidCoords[0], this.centroidCoords[1], this.meanMagnitude, this.meanDepth, this.predictedNextTime[0], this.predictedNextTime[1]
        ));
        report.append(String.format("> Total number of earthquake entries analysed - %d.\n",
            this.dataSet.length
        ));
        report.append("End of report.\n");
        report.append("Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n");

        return report.toString();
    }
    public String renderRawDataSet(){
        StringBuilder rawDataSet = new StringBuilder("Earthquake Data Set:\n");
        //^ Using StringBuilder is a better alternative for string concatenation.
        //^ This approach is more efficient for building string in terms of memory usage and performance.
        //^ Also is simpler for any extensibility needed (e.g., redirecting output to a logging file).
        for (int i = 0; i < this.dataSet.length; i++) {
            rawDataSet.append(String.format("> Index #%d %s\n", i, dataSet[i].toString()));
        }
        return rawDataSet.toString();
    }

}
