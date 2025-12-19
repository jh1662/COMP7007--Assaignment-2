import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Interpreter class implementing ClientActions interface.
 * Provides user interface loop and command methods for user interaction.
 * <p>
 * Makes use of HTTPRetriever and JSONToRecord singleton instances to retrieve and convert data from USGS api respectively.
 * Caches retrieved data set and generated reports for user viewing and exporting.
 */
public class Interpreter implements ClientActions {

    private final HTTPRetriever retriever = HTTPRetriever.getInstance();
    private final JSONToRecord converter = JSONToRecord.getInstance();

    private final Scanner scanner = new Scanner(System.in);

    private final ArrayList<EarthquakeReport> reports = new ArrayList<>();
    private EarthquakeEntry[] currentDataSet = null;
    private ZonedDateTime startTimeStamp;
    private ZonedDateTime endTimeStamp;

    /**
     * Main user interface loop - runs the user commands, and inform user, until user exits program.
     * Assures that the program keeps running until user decides to exit.
     * <p>
     * Catches exceptions, thrown by command methods, and informs user of the error instead of crashing the program.
     */
    public void interfacing() {
        while (true) {
            try { this.cycle(); }
            catch (Exception e) { System.out.println("ERROR ENCOUNTERED: " + e.getMessage()); }
        }
    }

    /**
     * Single cycle of user interface loop - process user command once.
     * Allows use to choose which command to run.
     * Called by 'this.interfacing()' method repeatedly.
     * <p>
     * Makes use of lambda expressions (specifically method references) to improve readability and maintainability.
     * A command map ('Map<String, Runnable>') is used for better performance compared to an enhanced switch statement.
     * <p>
     * Forwards thrown exceptions to 'this.interfacing()' method for handling.
     */
    private void cycle() {
        Map<String, Runnable> commandMap = Map.of(
            //* Map but the values are lambdas (specifically method references), instead of data, for better readability.
            "1", this::submitQuery,
            //^ Would be `case "1" -> this.submitQuery();` in a enhanced switch statement.
            "2", this::generateReport,
            "3", this::viewRawDataSet,
            "4", this::exportAllReports,
            "5", this::compareToPreviousReport,
            "6", this::exitProgram,
            "7", this::getManual
        );
        String command = processInput(InputType.INTEGER, "Enter command (type '7' for help): ").trim();
        //^ Take valid integer-parsable string user input for command selection.
        commandMap.getOrDefault(command, () -> System.out.println("Invalid command; please input a valid command number (1-7).")).run();
        //^ Run the corresponding command lambda, or print invalid command message as specified in the arg lambda.
    }

    /**
     * Processes user input according to expected input type.
     * Validates input and prompts user again if invalid input is given.
     * Assures user to not cause unexpected behavior by giving unexpected inputs.
     * <p>
     * Uses recursion to re-prompt user until valid input is given.
     * <p>
     * Different input types supported (declared by InputType enum):
     * <ul>
     *     <li>input string just as string - any non-empty string up to 512 characters.</li>
     *     <li>input string as integer - any string that is parsable to int (by 'Integer.parseInt').</li>
     *     <li>input string as decimal - any string that is parsable to double (by 'Double.parseDouble').</li>
     *     <li>input string as timestamp - any string in 'YYYY-MM-DD:HH' format (UTC time zone), and with valid number components (dictated by 'ZonedDateTime.parse').</li>
     * </ul>
     * @param inputType The expected type of user input.
     * @param promptMessage The message to prompt user before input.
     * @return The valid user input as string.
     * @throws InputMismatchException if user averted input (empty or only whitespace).
     */
    private String processInput(InputType inputType, String promptMessage) {
        //* Validates inputs according to expected inputs.
        System.out.println(promptMessage);
        //^ Prompt message, before input, so user knows what to put.

        String userInput = this.scanner.nextLine();
        if (userInput == null || userInput.isBlank()) throw new InputMismatchException("Input averted");
        //^ If user don't input anything (empty or only whitespace), we consider it as averted input.
        //^ Also how user wants to go back or cancel the current operation.

        //* Recursive call until valid input is given.
        //* We assume user will eventually give valid input before too many recursive calls causing stack overflow.
        switch (inputType) {
            //* Enhanced switch statement (using lambdas) for better readability.
            case STRING -> {
                //* Already checked if empty using 'isBlank'.
                if (userInput.length() > 512) {
                    System.out.println(userInput + "is excessively long input; must be 512 characters or less.");
                    return this.processInput(InputType.STRING, promptMessage);
                }
                //^ Prevents excessively long string inputs.
            }
            case INTEGER -> {
                try { Integer.parseInt(userInput); }
                catch (NumberFormatException e) {
                    System.out.println(userInput + "is invalid input; must be an integer.");
                    return this.processInput(InputType.INTEGER, promptMessage);
                }
            }
            case DECIMAL -> {
                try { Double.parseDouble(userInput); }
                catch (NumberFormatException e) {
                    System.out.println(userInput + "is invalid input; must be an decimal.");
                    return this.processInput(InputType.DECIMAL, promptMessage);
                }
            }
            case TIMESTAMP -> {
                //* This scope is the main reason for this method ('this.processInputs') to exist.
                //* String format expected: YYYY-MM-DD:HH (UTC is assumed).
                //* Because the local processing only uses up to by-the-hour precision, we only validate up to that - user won't input minutes and shorter scales.
                //: Validating timestamp requires many steps and checks - thus we use regex to simplify the process.
                String timestampCheckRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]):([01]\\d|2[0-3])$";
                //^ Not only checks the format, but also the ranges of month, day, and hour.
                if (!userInput.matches(timestampCheckRegex)) {
                    System.out.println(userInput + " is invalid input; must be in 'YYYY-MM-DD:HH' format (UTC time zone).");
                    return this.processInput(InputType.TIMESTAMP, promptMessage);
                }

                //: Checks the number components are within bounds.
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").parseStrict().toFormatter(Locale.ROOT);
                //^ Replacement to the faulty 'DateTimeFormatter.ofPattern("yyyy-MM-dd")'.withResolverStyle(ResolverStyle.STRICT); which did not allow ANYTHING.
                try { LocalDate.parse(userInput.split(":")[0], formatter); }
                catch (DateTimeParseException e) {
                    System.out.println(userInput + " is invalid input; unable to parse timestamp due to one of the number components being out of bounds.");
                    return this.processInput(InputType.TIMESTAMP, promptMessage);
                }
            }
            //! No default case needed as all enum cases are covered.
        }

        return userInput.trim();
    }

    /**
     * Converts validated timestamp string to ZonedDateTime object.
     * <p>
     * Assumes timestamp arg is valid - does not throw or forward any exceptions.
     * @param timestamp The validated timestamp string in 'YYYY-MM-DD:HH' format.
     * @return The corresponding ZonedDateTime object in UTC time zone.
     */
    private ZonedDateTime toZonedDateTime(String timestamp) {
        //* Called after successful validation of timestamp input.
        //* Converts string format 'YYYY-MM-DD:HH' to 'yyyy-mm-ddThh:mm:ssZ' parsable string, before parsing.
        String parsableTimestamp = timestamp.substring(0, 10) + "T" + timestamp.substring(11) + ":00:00Z";
        //^ Precise time scales (minutes and seconds) are set to zero.
        //^ 'Z' suffix indicates UTC time zone.
        //^ 'T' is the separator between date and time components in ISO8601 format.
        return ZonedDateTime.parse(parsableTimestamp);
    }

    /**
     * Command-like method sets up and submits query to USGS api according to user inputs.
     * Retrieve, decode, and convert the data set from the api response.
     * <p>
     * Stores the data set as 'this.currentDataSet' for viewing and report generation.
     * <p>
     * Forwards exceptions thrown by retriever and converter to caller method.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     * @throws IllegalArgumentException if any of the user inputs are invalid (out of bounds).
     */
    @Override
    public void submitQuery() {
        //: Stored as fields as its is not part of raw data set, but used in report generation.
        this.startTimeStamp = this.toZonedDateTime(this.processInput(InputType.TIMESTAMP, "Enter the start timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): "));
        this.endTimeStamp = this.toZonedDateTime(this.processInput(InputType.TIMESTAMP, "Enter the end timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): "));

        APIQuery query = new APIQuery(
            Integer.parseInt(this.processInput(InputType.INTEGER, "Enter the limit of earthquake entries to retrieve (0-20000): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the latitude of the center point of area (-90 to 90): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the longitude of the center point of area (-180 to 180): ")),
            Integer.parseInt(this.processInput(InputType.INTEGER, "Enter the maximum radius from center point to retrieve earthquake entries (in kilometers, 0 to 20000): ")),
            this.startTimeStamp,
            this.endTimeStamp,
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the minimum magnitude of earthquake entries to retrieve (-5.0 to 10.0): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the maximum magnitude of earthquake entries to retrieve (-5.0 to 10.0): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the minimum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): ")),
            Double.parseDouble(this.processInput(InputType.DECIMAL, "Enter the maximum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): "))
        );
        try { this.currentDataSet = (this.converter.convert(this.retriever.requestAPIDataRecord(query))); }
        catch (Exception e) { throw new IllegalArgumentException(e.getMessage()); }

        System.out.println("Query Submitted, responded, recorded, converted, and stored successfully.");
    }
    /**
     * Delegator method generates 'EarthquakeReport' record from 'this.currentDataSet' (cache basically) before displaying string representation.
     * Such record is also stored in 'this.report' for history view and comparison.
     * <p>
     * Forwards exceptions thrown due to empty data set to caller method.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     * @throws IllegalStateException if 'this.currentDataSet' is empty (null).
     */
    @Override
    public void generateReport() {
        if (this.currentDataSet == null) throw new IllegalStateException("Current data set empty; query the API first before generating report.");
        EarthquakeReport report = EarthquakeReport.of(this.currentDataSet, this.startTimeStamp, this.endTimeStamp);
        this.reports.add(report);
        System.out.println(report.toString());
    }
    /**
     * Presentation method displays the raw data set from 'this.currentDataSet' (cache).
     * <p>
     * Simply prints out, instead for throwing exceptions, if data set is instantiated but has no populations - results from zero-matches query.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     * @throws IllegalStateException if 'this.currentDataSet' is empty (null).
     */
    @Override
    public void viewRawDataSet() {
        if (this.currentDataSet == null) throw new IllegalStateException("Current data set empty; query the API first before viewing data set.");
        //^ Used state exception to indicate no data set available to view due to not querying first.
        if (this.currentDataSet.length == 0) {
            //* Edge case - zero entries retrieved from last query.
            //* Does not throw exception as there is nothing wrong.
            System.out.println("Current data set is empty; no earthquake entries retrieved from last query.");
            return;
        }
        StringBuilder rawDataSet = new StringBuilder("### Raw Data Set: ###\n");
        //^ Using 'StringBuilder' for efficient string concatenation in loops.
        IntStream.range(0, this.currentDataSet.length).forEach(i -> rawDataSet.append("> #").append(i + 1).append(" ").append(this.currentDataSet[i].toString()).append("\n"));
        //^ Replaces 'for (int i = 0; i < this.currentDataSet.length; i++) rawDataSet.append("> #").append(i + 1).append(" ").append(this.currentDataSet[i].toString()).append("\n");'
        //^ Used 'IntStream' instead of normal '.stream' to show index of each earthquake entry.
        //^ Append chain is preferred here, over string format, as most strings are small (except earthquake entry).
        rawDataSet.append("### ############# ###\n");
        System.out.println(rawDataSet.toString());
    }
    /**
     * Presentation method exports all cached reports ('this.reports') to console.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     * @throws IllegalStateException if no cached reports exist ('this.reports' being empty).
     */
    @Override
    public void exportAllReports() {
        if (this.reports.isEmpty()) throw new IllegalStateException("No cached reports to export; generate report first before exporting.");

        String allData = IntStream.range(0, this.reports.size())
            //^ 'IntStream' to show index of each report.
            //^ Stream replaces `for (int i = 0; i < this.reports.size(); i++) {`
            .mapToObj(i -> {
                EarthquakeReport report = this.reports.get(i);
                return new StringBuilder()
                    //^ Using 'StringBuilder' for efficient string concatenation in loops.
                    //^ Because 'StringBuilder' is not used outside lambda scope - hence is declared here.
                    .append("--- Report #").append(i + 1).append(" ---\n")
                    .append(report.toString()).append("\n")
                    .append("### Raw Data Set: ###\n")
                    .append(report.renderRawDataSet()).append("\n")
                    .append("### ############# ###\n")
                    .append("--- --------------- ---\n")
                    .toString();
            })
            .collect(Collectors.joining());
            //^ Concatenates all report strings.

        allData += "ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n";
        System.out.println(allData.toString());
    }
    /**
     * Presentation method compares the latest cached report to the previous cached report and displays the comparison.
     * <p>
     * Forwards exceptions thrown due to insufficient cached reports to caller method.
     * <p>
     * Because this a comparison between two analysis, complete precision is required - no rounding wanted here.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     * @throws IllegalStateException if less than two cached reports exist in 'this.reports'.
     */
    @Override
    public void compareToPreviousReport() {
        if (this.reports.isEmpty() || this.reports.size() < 2) throw new IllegalStateException("Not enough cached reports to compare; generate at least two reports before comparing.");

        System.out.println(this.reports.getLast().compareTo(this.reports.get(this.reports.size() - 2)));
        //^ `this.reports.getLast()` is shorthand for `this.reports.get(this.reports.size() - 1);`
    }
    /**
     * Terminal method exits the program safely.
     * <p>
     * Prints friendly exit message before exiting for responsiveness’s sake.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     */
    @Override
    public void exitProgram() {
        System.out.println("Exiting program... Goodbye valued customer!");
        //^ Friendly exit message.
        System.exit(0);
        //^ Exit program safely.
    }
    /**
     * Printer method displays user interface manual to console.
     * <p>
     * Uses multi-line string representation for better readability.
     * <p>
     * One of the actions available to user, as a command, in the user CLI (Command-Line Interface).
     */
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
