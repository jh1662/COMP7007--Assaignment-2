import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Developer MUST read this Javadoc to understand the testing structure.
 * <p>
 * This test suite focuses on testing the methods of 'Interpreter' class.
 * <p>
 * Private methods are accessed via reflection.
 * <p>
 * Console input is simulated by redirecting 'System.in' to a 'ByteArrayInputStream' containing predefined input data.
 * Meanwhile, console output is captured by redirecting 'System.out' to a 'ByteArrayOutputStream'.
 * <p>
 * Each tested methods is done in its own nested class, to separate concerns and improve readability.
 * Meanwhile, each test case is a method within the nested class to allow immediate pinpointing of which exact test case, rather than the just group of tests, has failed.
 *
 */
class RecordRetrieverTests {
    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        this.interpreter = new Interpreter();
    }

    @Test
    void testSingleCycle() throws Exception {
        String consoleInput =
            //^ Important - every input line ends with \n, including the last one.
            "7\n" +  "7\n" +
            "6\n" +
            "1900-01-01:00\n" +
            "2024-12-31:23\n" +
            "2000\n" +
            "0\n" +
            "0\n" +
            "20000\n" +
            "-5\n" +
            "10\n" +
            "1\n" +
            "800\n" +
            "6\n";   // <-- trailing newline is critical

        System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));
        this.interpreter.interfacing();
    }

    @Nested
    class Cycle {
        Method cycleMethod;
        PrintStream output;
        Interpreter interpreter;
        @BeforeEach
        void setUp() throws NoSuchMethodException {
            //: Access private 'Interpreter.cycle' method via reflection.
            this.cycleMethod = Interpreter.class.getDeclaredMethod("cycle");
            cycleMethod.setAccessible(true);
        }
        String runWithInput(String consoleInput) throws InvocationTargetException, IllegalAccessException {
            System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));
            //^ Set console input to our predefined input.

            //: Set up to capture console output.
            this.output = System.out;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            this.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.
            this.cycleMethod.invoke(this.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(output);
            //^ Prevents any side effects on other tests.

            return outputStream.toString();
        }
        String runWithInput(String consoleInput, String fieldName, Object newValue) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));
            //^ Set console input to our predefined input.

            //: Set up to capture console output.
            this.output = System.out;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            this.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.

            //: If any Field access modifications to 'this.interpreter' are needed, do them here;
            // this is because, unlike method modifications, they require the 'Interpreter' instance.
            Field modifiedField = Interpreter.class.getDeclaredField(fieldName);
            modifiedField.setAccessible(true);
            modifiedField.set(this.interpreter, newValue);

            this.cycleMethod.invoke(this.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(output);
            //^ Prevents any side effects on other tests.

            return outputStream.toString();
        }
        String runWithReports(String consoleInput, String fieldName, EarthquakeReport[] reports) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));
            //^ Set console input to our predefined input.

            //: Set up to capture console output.
            this.output = System.out;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            this.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.

            //: If any Field access modifications to 'this.interpreter' are needed, do them here;
            // this is because, unlike method modifications, they require the 'Interpreter' instance.
            Field modifiedField = Interpreter.class.getDeclaredField(fieldName);
            modifiedField.setAccessible(true);
            List<EarthquakeReport> reportFieldReference = (List<EarthquakeReport>) modifiedField.get(interpreter);
            Collections.addAll(reportFieldReference, reports);
            //^ Not a stream but does the job of adding multiple items to the list - replacing for-loop.

            this.cycleMethod.invoke(this.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(output);
            //^ Prevents any side effects on other tests.

            return outputStream.toString();
        }
        @AfterEach
        void tearDown() {
            System.setOut(this.output);
        }
        @Test
        void displayManual() {
            String expected =
                "Enter command (type '7' for help): \n" +
                //^ Prompt for user's command.
                "--- Welcome to the program! ---\n" +
                "> commands (enter the corresponding number to activate one one of the following):\n" +
                "> 1. api query - make and commit data query to the USGS api.\n" +
                "> 2. generate report - generate report from current data set (from latest api query) and view it; said report will be saved in cache.\n" +
                "> 3. view raw data set - view the current data set (from latest api query) in raw format .\n" +
                "> 4. export all reports - export all cached reports (with their respective raw data) to a text file.\n" +
                "> 5. compare to previous report - compare the latest report to the previous report (if both exists) and view the comparison.\n" +
                "> 6. exit program - exit the program safely.\n" +
                "> 7. help - view this manual again.\n" +
                "--- ----------------------- ---\n";
                //^ Extra newline at end due to 'println' call.

            String printed = "";
            try { printed = this.runWithInput("7\n6\n"); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
            //^ Please refer to the MS Word dev log to understand why this replacement is necessary.
        }

        //! Due to the nature of 'system.exit(0)', the exit ("6") command cannot be tested here.

        @Test
        void compareReports(){
            String expectedExceptionMsg = "This functionality is not yet implemented.";

            try { this.runWithInput("5\n6\n"); }
            catch (InvocationTargetException  e) {
                Throwable cause = e.getCause();
                //^ unwrap the real exception
                Assertions.assertInstanceOf(UnsupportedOperationException.class, cause);
                Assertions.assertEquals(expectedExceptionMsg, cause.getMessage());
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.
        }
        @Test
        void generateReport(){
            String expectedExceptionMsg = "This functionality is not yet implemented.";

            try { this.runWithInput("2\n6\n"); }
            catch (InvocationTargetException  e) {
                Throwable cause = e.getCause();
                //^ unwrap the real exception
                Assertions.assertInstanceOf(UnsupportedOperationException.class, cause);
                Assertions.assertEquals(expectedExceptionMsg, cause.getMessage());
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.
        }
        @Test
        void submitApiQuery(){

        }
        @Test
        void exitProgram(){

        }

        @Test
        @DisplayName("Attempt to print all reports BUT there are no reports cached/stored")
        void attemptExportAllReports(){
            String expectedExceptionMsg = "No cached reports to export; generate report first before exporting.";

            try { this.runWithInput("4\n6\n"); }
            catch (InvocationTargetException  e) {
                Throwable cause = e.getCause();
                //^ unwrap the real exception
                Assertions.assertInstanceOf(IllegalStateException.class, cause);
                Assertions.assertEquals(expectedExceptionMsg, cause.getMessage());
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.
        }
        @Test
        @DisplayName("Attempt to print all reports BUT there is only one report cached/stored (edge case)")
        void attemptExportAllReports2(){
            String expected = "Enter command (type '7' for help): \n" +
                "--- Report #1 ---\n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 5.1 with quartiles (Q1, Q2, and Q3) of 5.0 Mw, 5.1 Mw, and 5.1 Mw.\n" +
                "> Timing - mean intermission time of 0.0 hours with a monthly frequency of 182.50 earthquakes per month.\n" +
                "> Location - centroid at -35.000° N 95.000° E.\n" +
                "> Depth - mean of 407.50 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at -35.000° N, 95.000° E, with magnitude strength of 5.1 Mw hitting 407.50 Km deep, between 1970-01-19T11:24:03.600Z[UTC] and 1970-07-20T11:24:03.600Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in New York (at -90.000° N 180.000° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of 5.1Mw hitting a depth: 800.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n";

            //: Get some timestamps for the test.
            ZonedDateTime timestamp = ZonedDateTime.of(2020, 8, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime timestampStart = ZonedDateTime.of(2020, 7, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime timestampEnd = ZonedDateTime.of(2021, 7, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            String timestampUnix = Long.toString(timestamp.toEpochSecond());

            String printed = "";
            try {
                printed = this.runWithReports(
                    "4\n6\n",
                    "reports",
                    new EarthquakeReport[]{
                    EarthquakeReport.of(
                        new EarthquakeEntry[]{
                            new EarthquakeEntry(5.0, "Some random ahh place", timestampUnix, 10.0, 20.0, 15.0),
                            new EarthquakeEntry(5.1, "New York", timestampUnix, 180.0, -90.0, 800)
                        },
                        timestampStart,
                        timestampEnd
                    )
                });
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to print all reports and there are 3 reports cached/stored")
        void attemptExportAllReports3(){
            String expected = "Enter command (type '7' for help): \n" +
                "--- Report #1 ---\n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 5.1 with quartiles (Q1, Q2, and Q3) of 5.0 Mw, 5.1 Mw, and 5.1 Mw.\n" +
                "> Timing - mean intermission time of 0.0 hours with a monthly frequency of 182.50 earthquakes per month.\n" +
                "> Location - centroid at -35.000° N 95.000° E.\n" +
                "> Depth - mean of 407.50 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at -35.000° N, 95.000° E, with magnitude strength of 5.1 Mw hitting 407.50 Km deep, between 1970-01-19T11:24:03.600Z[UTC] and 1970-07-20T11:24:03.600Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in New York (at -90.000° N 180.000° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of 5.1Mw hitting a depth: 800.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "--- Report #2 ---\n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 2.5 with quartiles (Q1, Q2, and Q3) of -5.0 Mw, 2.5 Mw, and 10.0 Mw.\n" +
                "> Timing - mean intermission time of 0.0 hours with a monthly frequency of 182.50 earthquakes per month.\n" +
                "> Location - centroid at 12.562° N 61.750° E.\n" +
                "> Depth - mean of 8.00 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at 12.562° N, 61.750° E, with magnitude strength of 2.5 Mw hitting 8.00 Km deep, between 1970-01-19T11:24:03.600Z[UTC] and 1970-07-20T11:24:03.600Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in United States of England (at 20.000° N 23.500° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of -5.0Mw hitting a depth: 15.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in Principality of Mars (at 5.123° N 99.999° E) happened at 1970-01-19T11:24:03.600Z[UTC] with a magnitude of 10.0Mw hitting a depth: 1.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n";

            //: Get some timestamps for the test.
            ZonedDateTime timestamp = ZonedDateTime.of(2020, 8, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime timestampStart = ZonedDateTime.of(2020, 7, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime timestampEnd = ZonedDateTime.of(2021, 7, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            String timestampUnix = Long.toString(timestamp.toEpochSecond());

            String printed = "";
            try {
                printed = this.runWithReports(
                    "4\n6\n",
                    "reports",
                    new EarthquakeReport[]{
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                new EarthquakeEntry(5.0, "Some random ahh place", timestampUnix, 10.0, 20.0, 15.0),
                                new EarthquakeEntry(5.1, "New York", timestampUnix, 180.0, -90.0, 800)
                            },
                            timestampStart,
                            timestampEnd
                        ),
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                new EarthquakeEntry(-5, "United States of England", timestampUnix, 23.5, 20.0, 15.0),
                                new EarthquakeEntry(10, "Principality of Mars", timestampUnix, 99.999, 5.123, 1)
                            },
                            timestampStart,
                            timestampEnd
                        ),
                    });
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        @Test
        @DisplayName("Attempt to print raw data with empty data set - without prior API query")
        //^ '@DisplayName' are used when multiple method names are the same variation; for example "attemptPrintRawData" and "attemptPrintRawData2".
        void attemptPrintRawData(){
            String expectedExceptionMsg = "Current data set empty; query the API first before viewing data set.";

            try { this.runWithInput("3\n6\n"); }
            catch (InvocationTargetException  e) {
                Throwable cause = e.getCause();
                //^ unwrap the real exception
                Assertions.assertInstanceOf(IllegalStateException.class, cause);
                Assertions.assertEquals(expectedExceptionMsg, cause.getMessage());
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.
        }

        @Test
        @DisplayName("Attempt to print raw data with empty data set - but the API query had no results")
        void attemptPrintRawData2() throws NoSuchFieldException, IllegalAccessException {
            String expected = "Enter command (type '7' for help): \n" +
                "Current data set is empty; no earthquake entries retrieved from last query.";

            //: Reflection code to simulate an API query that returned empty results - no found Earthquake instances.
            Field rawDataFieldReference = Interpreter.class.getDeclaredField("currentDataSet");
            rawDataFieldReference.setAccessible(true);
            rawDataFieldReference.set(this.interpreter, new EarthquakeEntry[0]);
            //^ Set 'interpreter.currentDataSet' to an empty array to simulate no results

            String printed = "";
            try { printed = this.runWithInput("3\n6\n"); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        @Test
        @DisplayName("Print raw data with empty data set BUT the API query only returned one result (edge case)")
        void attemptPrintRawData3() throws NoSuchFieldException, IllegalAccessException {
            String expected = "Enter command (type '7' for help): \n" +
                "### Raw Data Set: ###\n" +
                "> #1 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at 1970-01-19T10:40:04.800Z[UTC] with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "### ############# ###\n";

            //: Get some timestamp in epoch seconds for the test.
            ZonedDateTime timestamp = ZonedDateTime.of(2020, 7, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
            String timestampUnix = Long.toString(timestamp.toEpochSecond());

            String printed = "";
            try {
                printed = this.runWithInput("3\n6\n", "currentDataSet", new EarthquakeEntry[]{
                    new EarthquakeEntry(5.0, "Some random ahh place", timestampUnix, 10.0, 20.0, 15.0)
                });
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        @Test
        @DisplayName("Print raw data with empty data set and the API query returned two results (normal case)")
        void attemptPrintRawData4() throws NoSuchFieldException, IllegalAccessException {
            String expected = "Enter command (type '7' for help): \n" +
                "### Raw Data Set: ###\n" +
                "> #1 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at 1970-01-19T10:40:04.800Z[UTC] with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "> #2 earthquake entry: earthquake in New York (at -90.000° N 180.000° E) happened at 1970-01-19T10:40:04.800Z[UTC] with a magnitude of 5.1Mw hitting a depth: 800.00 km)]\n" +
                "### ############# ###\n";

            //: Get some timestamp in epoch seconds for the test.
            ZonedDateTime timestamp = ZonedDateTime.of(2020, 7, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
            String timestampUnix = Long.toString(timestamp.toEpochSecond());

            String printed = "";
            try {
                printed = this.runWithInput("3\n6\n", "currentDataSet", new EarthquakeEntry[]{
                    new EarthquakeEntry(5.0, "Some random ahh place", timestampUnix, 10.0, 20.0, 15.0),
                    new EarthquakeEntry(5.1, "New York", timestampUnix, 180.0, -90.0, 800)
                });
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }


    }




    @AfterEach
    void tearDown() {
        // Reset System.in if needed
        System.setIn(System.in);
    }
}