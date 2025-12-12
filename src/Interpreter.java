import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Interpreter implements ClientActions {

    private final HTTPRetriever retriever = HTTPRetriever.getInstance();
    private final JSONToRecord converter = JSONToRecord.getInstance();

    private final ArrayList<EarthquakeReport> reports = new ArrayList<>();
    private EarthquakeEntry[] currentDataSet = null;
    private ZonedDateTime startTimeStamp;
    private ZonedDateTime endTimeStamp;

    public void interfacing() {
        while (true) {
            try { this.cycle(); }
            catch (Exception e) { System.out.println("ERROR ENCOUNTERED: " + e.getMessage()); }
        }
    }

    private void cycle() {
        String command = processInput(InputType.INTEGER, "Enter command (type '7' for help): ");
        switch (command) {
            case "1" -> this.submitQuery();
            case "2" -> this.generateReport();
            case "3" -> this.viewRawDataSet();
            case "4" -> this.exportAllReports();
            case "5" -> this.compareToPreviousReport();
            case "6" -> this.exitProgram();
            case "7" -> this.getManual();
            default -> System.out.println("Invalid command; please input a valid command number (1-7).");
        }
    }

    private String processInput(InputType inputType, String promptMessage) {
        //* Validates inputs according to expected inputs.
        System.out.println(promptMessage);
        //^ Prompt message, before input, so user knows what to put.

        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        if (userInput.isBlank()) throw new InputMismatchException("Input averted");
        //^ If user don't input anything (empty or only whitespace), we consider it as averted input.
        //^ Also how user wants to go back or cancel the current operation.

        //* Recursive call until valid input is given.
        //* We assume user will eventually give valid input before too many recursive calls causing stack overflow.
        switch (inputType) {
            //* Enhanced switch statement (using lambdas) for better readability.
            case InputType.STRING -> {
                //* Already checked if empty using 'isBlank'.
                if (userInput.length() > 60) {
                    System.out.println(userInput + "is excessively long input; must be 60 characters or less.");
                    this.processInput(InputType.STRING, promptMessage);
                }
                //^ Prevents excessively long string inputs.
            }
            case InputType.INTEGER -> {
                try { Integer.parseInt(userInput); }
                catch (NumberFormatException e) {
                    System.out.println(userInput + "is invalid input; must be an integer.");
                    this.processInput(InputType.INTEGER, promptMessage);
                }
            }
            case InputType.DECIMAL -> {
                try { Double.parseDouble(userInput); }
                catch (NumberFormatException e) {
                    System.out.println(userInput + "is invalid input; must be an decimal.");
                    this.processInput(InputType.DECIMAL, promptMessage);
                }
            }
            case InputType.TIMESTAMP -> {
                //* This scope is the main reason for this method ('this.processInputs') to exist.
                //* String format expected: YYYY-MM-DD:HH (UTC is assumed).
                //* Because the local processing only uses up to by-the-hour precision, we only validate up to that - user won't input minutes and shorter scales.
                //: Validating timestamp requires many steps and checks - thus we use regex to simplify the process.
                String timestampCheckRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]):([01]\\d|2[0-3])$";
                //^ Not only checks the format, but also the ranges of month, day, and hour.
                if (!userInput.matches(timestampCheckRegex)) {
                    System.out.println(userInput + " is invalid input; must be in 'YYYY-MM-DD:HH' format (UTC time zone).");
                    this.processInput(InputType.TIMESTAMP, promptMessage);
                }
            }
        }

        return userInput;
    }

    private ZonedDateTime toZonedDateTime(String timestamp) {
        //* Called after successful validation of timestamp input.
        //* Converts string format 'YYYY-MM-DD:HH' to 'yyyy-mm-ddThh:mm:ssZ' parsable string, before parsing.
        String parsableTimestamp = timestamp.substring(0, 10) + "T" + timestamp.substring(11) + ":00:00Z";
        //^ Precise time scales (minutes and seconds) are set to zero.
        //^ 'Z' suffix indicates UTC time zone.
        //^ 'T' is the separator between date and time components in ISO8601 format.
        return ZonedDateTime.parse(parsableTimestamp);
    }

    @Override
    public void submitQuery() {
        APIQuery query = new APIQuery(
            Integer.parseInt(this.processInput(InputType.INTEGER, "Enter the limit of earthquake entries to retrieve (0-20000): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the latitude of the center point of area (-90 to 90): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the longitude of the center point of area (-180 to 180): ")),
            Integer.parseInt(this.processInput(InputType.INTEGER, "Enter the maximum radius from center point to retrieve earthquake entries (in kilometers, 0 to 20001.6): ")),
            this.toZonedDateTime(this.processInput(InputType.TIMESTAMP, "Enter the start timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): ")),
            this.toZonedDateTime(this.processInput(InputType.TIMESTAMP, "Enter the end timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the minimum magnitude of earthquake entries to retrieve (-5.0 to 10.0): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the maximum magnitude of earthquake entries to retrieve (-5.0 to 10.0): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the minimum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the maximum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): "))
        );
        try { this.currentDataSet = (this.converter.convert(this.retriever.requestAPIDataRecord(query))); }
        catch (Exception e) { throw new IllegalArgumentException(e.getMessage()); }

        System.out.println("Query Submitted, responded, recorded, converted, and stored successfully.");
    }
    @Override
    public void generateReport() {
        if (this.currentDataSet == null) throw new IllegalStateException("Current data set empty; query the API first before generating report.");
        EarthquakeReport report = EarthquakeReport.of(this.currentDataSet, this.startTimeStamp, this.endTimeStamp);
        this.reports.add(report);
        System.out.println(report.toString());
    }
    @Override
    public void viewRawDataSet() {
        if (this.currentDataSet == null) throw new IllegalStateException("Current data set empty; query the API first before viewing data set.");
        //^ Used state exception to indicate no data set available to view due to not querying first.
        if (this.currentDataSet.length == 0) System.out.println("Current data set is empty; no earthquake entries retrieved from last query.");
        //^ Edge case - zero entries retrieved from last query.
        //^ Does not throw exception as there is nothing wrong.
        StringBuilder rawDataSet = new StringBuilder();
        //^ Using 'StringBuilder' for efficient string concatenation in loops.
        for (int i = 0; i <= this.currentDataSet.length; i++) rawDataSet.append("Entry #").append(i + 1).append(this.currentDataSet[i].toString()).append("\n");
        //^ Used for-loop instead of enhanced for-loop to show index of each earthquake entry.
        //^ Append chain is preferred here, over string format, as most strings are small (except earthquake entry).
        System.out.println(rawDataSet.toString());
    }
    @Override
    public void exportAllReports() {
        if (this.reports.isEmpty()) throw new IllegalStateException("No cached reports to export; generate report first before exporting.");

        StringBuilder allData = new StringBuilder();
        //^ Using 'StringBuilder' for efficient string concatenation in loops.
        for (int i = 0; i < this.reports.size(); i++) {
            EarthquakeReport report = this.reports.get(i);
            allData.append("--- Report #").append(i + 1).append(" ---\n");
            allData.append(report.toString()).append("\n");
            allData.append("### Raw Data Set: ###\n");
            allData.append(report.renderRawDataSet()).append("\n");
            allData.append("### ############# ###\n");
            allData.append("--- --------------- ---\n");
        }
        allData.append("ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n");
        System.out.println(allData.toString());
    }
    @Override
    public void compareToPreviousReport() {
        throw new UnsupportedOperationException("This functionality is not yet implemented.");
    }
    @Override
    public void exitProgram() {
        System.out.println("Exiting program... Goodbye valued customer!");
        //^ Friendly exit message.
        System.exit(0);
        //^ Exit program safely.
    }
    @Override
    public void getManual() {
        System.out.println(
            //* user interface manual.
            //* Multi-line string representation for readability.
            "--- Welcome to the program! ---\n" +
            "> commands (enter the corresponding number to activate one one of the following):\n" +
            "> 1. api query - make and commit data query to the USGS api.\n" +
            "> 2. generate report - generate report from current data set (from latest api query) and view it; said report will be saved in cache.\n" +
            "> 3. view raw data set - view the current data set (from latest api query) in raw format .\n" +
            "> 4. export all reports - export all cached reports (with their respective raw data) to a text file.\n" +
            "> 5. compare to previous report - compare the latest report to the previous report (if both exists) and view the comparison.\n" +
            "> 6. exit program - exit the program safely.\n" +
            "> 7. help - view this manual again.\n" +
            "--- ----------------------- ---\n"
        );
    }
}
