import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
 */
class InterpreterTestingTests {
    static ByteArrayOutputStream outputStream;
    static PrintStream output;
    static Interpreter interpreter;

    /**
     * Table of test cases:
     * <table border="1">
     *   <caption>Unit test cases in InterpreterTestingTests.ProcessingInputs</caption>
     *   <thead>
     *     <tr>
     *       <th>Method</th>
     *       <th>@DisplayName</th>
     *       <th>Intent</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>avertInputError</td>
     *       <td>When user want to not input, they can avert it by simply inputting empty line/string (error is handled in 'Interpreter.interfacing')</td>
     *       <td>Empty/blank input triggers InputMismatchException with message Input averted.</td>
     *     </tr>
     *
     *     <tr>
     *       <td>integerInput</td>
     *       <td>Successfully process valid integer input - normal case scenario</td>
     *       <td>Accepts a normal integer input.</td>
     *     </tr>
     *     <tr>
     *       <td>integerInput2</td>
     *       <td>Successfully process valid integer input as zero (zero case)</td>
     *       <td>Accepts zero.</td>
     *     </tr>
     *     <tr>
     *       <td>integerInput3</td>
     *       <td>Successfully process valid integer input as negative (boundary? case)</td>
     *       <td>Accepts negative integer.</td>
     *     </tr>
     *     <tr>
     *       <td>integerInput4</td>
     *       <td>Successfully process valid integer input as with whitespaces on both sides (boundary? case)</td>
     *       <td>Rejects whitespace-padded token then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>integerInput5</td>
     *       <td>Attempts to process valid integer input as decimal; inputs whole number after user mistake (boundary? case)</td>
     *       <td>Rejects decimal then succeeds on retry with integer.</td>
     *     </tr>
     *     <tr>
     *       <td>integerInput6</td>
     *       <td>Attempt to process valid integer input BUT is written/typed in English words, so then u do it again correctly</td>
     *       <td>Rejects non-numeric token then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput</td>
     *       <td>Successfully process valid decimal input - normal case scenario</td>
     *       <td>Accepts a normal decimal input.</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput2</td>
     *       <td>Successfully process valid decimal input as zero (zero case)</td>
     *       <td>Accepts zero.</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput3</td>
     *       <td>Successfully process valid decimal input as negative (boundary? case)</td>
     *       <td>Accepts negative decimal.</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput4</td>
     *       <td>Successfully process valid decimal input as with whitespaces on both sides (boundary? case)</td>
     *       <td>Accepts formatted decimal input in this scenario.</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput5</td>
     *       <td>Attempts to process valid decimal input as whole number (normal case - same format as 'INTEGER' in this scenario)</td>
     *       <td>Accepts decimal-parsable input (test case description indicates whole number equivalence).</td>
     *     </tr>
     *     <tr>
     *       <td>decimalInput6</td>
     *       <td>Attempt to process valid decimal input BUT is written/typed in English words, so then u do it again correctly</td>
     *       <td>Rejects non-numeric token then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>stringInput</td>
     *       <td>normal case scenario for string input</td>
     *       <td>Accepts a normal string input.</td>
     *     </tr>
     *     <tr>
     *       <td>stringInput2</td>
     *       <td>attempt to process string input but its too long (>512 chars)</td>
     *       <td>Rejects overly long input then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>timestampInput</td>
     *       <td>normal case scenario for timestamp input</td>
     *       <td>Accepts correct YYYY-MM-DD:HH timestamp.</td>
     *     </tr>
     *     <tr>
     *       <td>timestampInput2</td>
     *       <td>Incorrect format for timestamp input (separators), then corrects it</td>
     *       <td>Rejects wrong separators then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>timestampInput3</td>
     *       <td>correct timestamp format but out of bound number (hour), then corrects it</td>
     *       <td>Rejects out-of-range hour then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>timestampInput5</td>
     *       <td>correct timestamp format but out of bound number (month), then corrects it</td>
     *       <td>Rejects out-of-range month then succeeds on retry.</td>
     *     </tr>
     *     <tr>
     *       <td>timestampInput6</td>
     *       <td>Incorrect format for timestamp input (numbers), then corrects it</td>
     *       <td>Rejects invalid numeric format then succeeds on retry.</td>
     *     </tr>
     *   </tbody>
     * </table>
     */
    @Nested
    class ProcessingInputs {
        Method processInputMethod;
        PrintStream output;

        @BeforeEach
        void setUp() throws NoSuchMethodException {
            //: Access private 'Interpreter.cycle' method via reflection.
            this.processInputMethod = Interpreter.class.getDeclaredMethod("processInput", InputType.class, String.class);
            processInputMethod.setAccessible(true);
        }

        String runWithInput(String consoleInput, InputType inputType) throws InvocationTargetException, IllegalAccessException {
            InterpreterTestingTests.setIO(consoleInput);
            //^ Set console input and output, but not teardown/restore.

            InterpreterTestingTests.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.
            this.processInputMethod.invoke(InterpreterTestingTests.interpreter, inputType, "Text for asking for specific input");
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(InterpreterTestingTests.output);
            //^ Prevents any side effects on other tests.

            return InterpreterTestingTests.outputStream.toString();
        }

        @Test
        @DisplayName("When user want to not input, they can avert it by simply inputting empty line/string (error is handled in 'Interpreter.interfacing')")
        void avertInputError() {
            String expectedExceptionMsg = "Input averted";

            try { this.runWithInput("\n", InputType.STRING); }
            catch (InvocationTargetException  e) {
                Throwable cause = e.getCause();
                //^ unwrap the real exception
                Assertions.assertInstanceOf(InputMismatchException.class, cause);
                Assertions.assertEquals(expectedExceptionMsg, cause.getMessage());
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }
        }

        //: Integer input tests.
        @Test
        @DisplayName("Successfully process valid integer input - normal case scenario")
        void integerInput() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("42\n", InputType.INTEGER);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid integer input as zero (zero case)")
        void integerInput2() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("0\n", InputType.INTEGER);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid integer input as negative (boundary? case)")
        void integerInput3() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("-42\n", InputType.INTEGER);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid integer input as with whitespaces on both sides (boundary? case)")
        void integerInput4() {
            String expected = "Text for asking for specific input\n" +
                " 42 is invalid input; must be an integer.\n" +
                "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput(" 42 \n42\n", InputType.INTEGER);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempts to process valid integer input as decimal; inputs whole number after user mistake (boundary? case)")
        void integerInput5() {
            String expected = "Text for asking for specific input\n" +
                "42.1is invalid input; must be an integer.\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("42.1\n42\n", InputType.INTEGER); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to process valid integer input BUT is written/typed in English words, so then u do it again correctly")
        void integerInput6() {
            String expected = "Text for asking for specific input\n" +
                "Twenty-oneis invalid input; must be an integer.\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("Twenty-one\n21\n", InputType.INTEGER); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        //: Decimal input tests.
        @Test
        @DisplayName("Successfully process valid decimal input - normal case scenario")
        void decimalInput() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("42.1\n", InputType.DECIMAL);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid decimal input as zero (zero case)")
        void decimalInput2() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("0\n", InputType.DECIMAL);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid decimal input as negative (boundary? case)")
        void decimalInput3() {
            String expected = "Text for asking for specific input";

            String printed = "";
            try {
                printed = this.runWithInput("-42.1\n", InputType.DECIMAL);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully process valid decimal input as with whitespaces on both sides (boundary? case)")
        void decimalInput4() {
            String expected = "Text for asking for specific input\n";

            String printed = "";
            try {
                printed = this.runWithInput(" 42.1 \n42.1", InputType.DECIMAL);
            } catch (Exception e) {
                Assertions.fail("Exception thrown during test: " + e.getCause());
            }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempts to process valid decimal input as whole number (normal case - same format as 'INTEGER' in this scenario)")
        void decimalInput5() {
            String expected = "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("42.1\n", InputType.DECIMAL); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to process valid decimal input BUT is written/typed in English words, so then u do it again correctly")
        void decimalInput6() {
            String expected = "Text for asking for specific input\n" +
                "Twenty-oneis invalid input; must be an decimal.\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("Twenty-one\n21\n", InputType.DECIMAL); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        //: String input tests.
        @Test
        @DisplayName("normal case scenario for string input")
        void stringInput() {
            String expected = "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("Random ahh place", InputType.STRING); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("attempt to process string input but its too long (>512 chars)")
        void stringInput2() {
            String expected = "Text for asking for specific input\n" +
                "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLis excessively long input; must be 512 characters or less.\n" +
                //^ 'L' repeated 513 times in this warning message.
                "Text for asking for specific input";

            String printed = "";
            try { printed = this.runWithInput("L".repeat(513)+"\nL\n", InputType.STRING); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        //: Timestamp input tests.
        @Test
        @DisplayName("normal case scenario for timestamp input")
        void timestampInput() {
            String expected = "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("2000-12-09:20\n", InputType.TIMESTAMP); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        //: Timestamp input tests.
        @Test
        @DisplayName("Incorrect format for timestamp input (separators), then corrects it")
        void timestampInput2() {
            String expected = "Text for asking for specific input\n" +
                "2000-12-09-20 is invalid input; must be in 'YYYY-MM-DD:HH' format (UTC time zone).\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("2000-12-09-20\n2000-12-09:20\n", InputType.TIMESTAMP); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("correct timestamp format but out of bound number (hour), then corrects it")
        void timestampInput3() {
            String expected = "Text for asking for specific input\n" +
                "2000-12-09:25 is invalid input; must be in 'YYYY-MM-DD:HH' format (UTC time zone).\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("2000-12-09:25\n2000-12-09:20\n", InputType.TIMESTAMP); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("correct timestamp format but out of bound number (month), then corrects it")
        void timestampInput5() {
            String expected = "Text for asking for specific input\n" +
                "2000-13-09:25 is invalid input; must be in 'YYYY-MM-DD:HH' format (UTC time zone).\n" +
                "Text for asking for specific input\n";

            String printed = "";
            try { printed = this.runWithInput("2000-13-09:25\n2000-12-09:20\n", InputType.TIMESTAMP); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
    }

    /**
     * Table of test cases:
     * <table border="1">
     *   <thead>
     *     <tr>
     *       <th>Method</th>
     *       <th>@DisplayName</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>displayManual</td>
     *       <td>Successfully display manual/help information to user</td>
     *     </tr>
     *     <tr>
     *       <td>attemptCompareReports</td>
     *       <td>Attempt to compare reports BUT there is no report cached/stored</td>
     *     </tr>
     *     <tr>
     *       <td>attemptCompareReports2</td>
     *       <td>Attempt to compare reports BUT there is only one report cached/stored</td>
     *     </tr>
     *     <tr>
     *       <td>compareReports</td>
     *       <td>Successfully compare two cached/stored reports</td>
     *     </tr>
     *     <tr>
     *       <td>attemptGenerateReport</td>
     *       <td>Attempt to generate report BUT there is no current data set</td>
     *     </tr>
     *     <tr>
     *       <td>attemptGenerateReport2</td>
     *       <td>Attempt to generate report BUT there is only one entry in the current data set (edge case)</td>
     *     </tr>
     *     <tr>
     *       <td>attemptGenerateReport3</td>
     *       <td>Attempt to generate report and there are only 2 entries in the current data set (normal case scenario)</td>
     *     </tr>
     *     <tr>
     *       <td>submitApiQuery</td>
     *       <td>Successfully submit API query with edge values</td>
     *     </tr>
     *     <tr>
     *       <td>attemptExportAllReports</td>
     *       <td>Attempt to print all reports BUT there are no reports cached/stored</td>
     *     </tr>
     *     <tr>
     *       <td>attemptExportAllReports2</td>
     *       <td>Attempt to print all reports BUT there is only one report cached/stored (edge case)</td>
     *     </tr>
     *     <tr>
     *       <td>attemptExportAllReports3</td>
     *       <td>Attempt to print all reports and there are 2 reports cached/stored</td>
     *     </tr>
     *     <tr>
     *       <td>attemptPrintRawData</td>
     *       <td>Attempt to print raw data with empty data set - without prior API query</td>
     *     </tr>
     *     <tr>
     *       <td>attemptPrintRawData2</td>
     *       <td>Attempt to print raw data with empty data set - but the API query had no results</td>
     *     </tr>
     *     <tr>
     *       <td>attemptPrintRawData3</td>
     *       <td>Print raw data with empty data set BUT the API query only returned one result (edge case)</td>
     *     </tr>
     *     <tr>
     *       <td>attemptPrintRawData4</td>
     *       <td>Print raw data with empty data set and the API query returned two results (normal case)</td>
     *     </tr>
     *   </tbody>
     * </table>
     */
    @Nested
    class Cycle {
        Method cycleMethod;
        PrintStream output;
        /**
         * Gives access to private 'Interpreter.cycle' method, via reflection, before each test case.
         * @throws NoSuchMethodException if 'Interpreter.cycle' method does not exist.
         */
        @BeforeEach
        void setUp() throws NoSuchMethodException {
            //: Access private 'Interpreter.cycle' method via reflection.
            this.cycleMethod = Interpreter.class.getDeclaredMethod("cycle");
            cycleMethod.setAccessible(true);
        }
        /**
         * Overloaded helper method, expands on 'InterpreterTestingTests.setIO' to set console input and output streams for the current 'Interpreter' instance.
         * <p>
         * It is critical that Interpreter is instantiated after 'InterpreterTestingTests.setIO' to prevent "no line found" exceptions.
         * @param consoleInput String representing the predefined console input.
         */
        String runWithInput(String consoleInput) throws InvocationTargetException, IllegalAccessException {
            InterpreterTestingTests.setIO(consoleInput);
            //^ Set console input and output, but not teardown/restore.

            InterpreterTestingTests.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.
            this.cycleMethod.invoke(InterpreterTestingTests.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(InterpreterTestingTests.output);
            //^ Prevents any side effects on other tests.

            return InterpreterTestingTests.outputStream.toString();
        }
        /**
         * Overloaded helper method, expands on 'InterpreterTestingTests.setIO' to set console input and output streams for the current 'Interpreter' instance.
         * <p>
         * It is critical that Interpreter is instantiated after 'InterpreterTestingTests.setIO' to prevent "no line found" exceptions.
         * <p>
         * Additionally, modifies a specified Field of the 'Interpreter' instance to a new value before invoking 'cycle' method.
         * This allows testing of 'cycle' method under different internal states of the 'Interpreter' instance.
         * @param consoleInput String representing the predefined console input.
         * @param fieldName String representing the name of the Field to modify.
         * @param newValue Object representing the new value to set for the specified Field.
         */
        String runWithInput(String consoleInput, String fieldName, Object newValue, ZonedDateTime startTimestamp, ZonedDateTime endTimestamp) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            InterpreterTestingTests.setIO(consoleInput);
            //^ Set console input and output, but not teardown/restore.

            InterpreterTestingTests.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.

            //: If any Field access modifications to 'this.interpreter' are needed, do them here;
            // this is because, unlike method modifications, they require the 'Interpreter' instance.
            Field modifiedField = Interpreter.class.getDeclaredField(fieldName);
            modifiedField.setAccessible(true);
            modifiedField.set(InterpreterTestingTests.interpreter, newValue);

            if (startTimestamp != null && endTimestamp != null) {
                //: Modify start and end timestamps for report generation (as context/metadata).
                Field modifiedStartTimestamp = Interpreter.class.getDeclaredField("startTimeStamp");
                modifiedStartTimestamp.setAccessible(true);
                modifiedStartTimestamp.set(InterpreterTestingTests.interpreter, startTimestamp);
                Field modifiedEndTimestamp = Interpreter.class.getDeclaredField("endTimeStamp");
                modifiedEndTimestamp.setAccessible(true);
                modifiedEndTimestamp.set(InterpreterTestingTests.interpreter, endTimestamp);
            }

            this.cycleMethod.invoke(InterpreterTestingTests.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(output);
            //^ Prevents any side effects on other tests.

            return outputStream.toString();
        }
        /**
         * Helper method, expands on 'InterpreterTestingTests.setIO' to set console input and output streams for the current 'Interpreter' instance.
         * <p>
         * It is critical that Interpreter is instantiated after 'InterpreterTestingTests.setIO' to prevent "no line found" exceptions.
         * <p>
         * Additionally, modifies a specified Field of the 'Interpreter' instance to a new value before invoking 'cycle' method.
         * This allows testing of 'cycle' method under different internal states of the 'Interpreter' instance.
         * <p>
         * Due to the nature of 'system.exit(0)', the exit ("6") command cannot be tested here.
         * @param consoleInput String representing the predefined console input.
         * @param fieldName String representing the name of the Field to modify.
         * @param reports EarthquakeReport[] representing the new value to set for the specified Field.
         */
        String runWithReports(String consoleInput, String fieldName, EarthquakeReport[] reports, ZonedDateTime startTimestamp, ZonedDateTime endTimestamp) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            InterpreterTestingTests.setIO(consoleInput);
            //^ Set console input and output, but not teardown/restore.

            InterpreterTestingTests.interpreter = new Interpreter();
            //^ Called here so System.in is set to the predefined input.

            //: If any Field access modifications to 'this.interpreter' are needed, do them here;
            // this is because, unlike method modifications, they require the 'Interpreter' instance.
            Field modifiedReports = Interpreter.class.getDeclaredField(fieldName);
            modifiedReports.setAccessible(true);
            List<EarthquakeReport> reportFieldReference = (List<EarthquakeReport>) modifiedReports.get(InterpreterTestingTests.interpreter);
            Collections.addAll(reportFieldReference, reports);
            //^ Not a stream but does the job of adding multiple items to the list - replacing for-loop.

            //: Modify start and end timestamps for report generation (as context/metadata).
            Field modifiedStartTimestamp = Interpreter.class.getDeclaredField("startTimeStamp");
            modifiedStartTimestamp.setAccessible(true);
            modifiedStartTimestamp.set(InterpreterTestingTests.interpreter, startTimestamp);
            Field modifiedEndTimestamp = Interpreter.class.getDeclaredField("endTimeStamp");
            modifiedEndTimestamp.setAccessible(true);
            modifiedEndTimestamp.set(InterpreterTestingTests.interpreter, endTimestamp);

            this.cycleMethod.invoke(InterpreterTestingTests.interpreter);
            //^ Called here, instead of in 'this.setUp', to prevent "no line found" exceptions.

            System.setOut(output);
            //^ Prevents any side effects on other tests.

            return outputStream.toString();
        }
        @Test
        @DisplayName("Successfully display manual/help information to user")
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
            try { printed = this.runWithInput("7\n"); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
            //^ Please refer to the MS Word dev log to understand why this replacement is necessary.
        }

        //! Due to the nature of 'system.exit(0)', the exit ("6") command cannot be tested here.

        @Test
        @DisplayName("Attempt to compare reports BUT there is no report cached/stored")
        void attemptCompareReports(){
            String expectedExceptionMsg = "Not enough cached reports to compare; generate at least two reports before comparing.";

            String printed = "";
            try {
                printed = this.runWithInput("5\n", "reports",
                    new ArrayList<>(Arrays.asList(
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(1),
                            InterpreterTestingTests.giveExampleTimestamp(4)
                        )
                    )),
                    null,
                    null
                );
            }
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
        @DisplayName("Attempt to compare reports BUT there is only one report cached/stored")
        void attemptCompareReports2(){
            String expectedExceptionMsg = "Not enough cached reports to compare; generate at least two reports before comparing.";

            try {
                this.runWithInput("5\n", "reports",
                    new ArrayList<>(Arrays.asList(
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(1),
                            InterpreterTestingTests.giveExampleTimestamp(4)
                        )
                    )),
                    null,
                    null
                );
            }
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
        @DisplayName("Successfully compare two cached/stored reports")
        void compareReports(){
            String expected = "Enter command (type '7' for help): \n" +
                "--- Comparison of Latest Report to Previous Report ---\n" +
                "> Both reports have the same filtered earthquake entry count of 2.\n" +
                "> Previous report has a higher mean magnitude of 7.5 Mw.\n" +
                "> Latest report has a higher mean depth of 407.5 Km.\n" +
                "> Latest report has a higher monthly frequency of 0.0 times per month.\n" +
                "--- ----------------------------------------- ---\n";

            String printed = "";
            try {
                printed = this.runWithInput("5\n", "reports",
                    new ArrayList<>(Arrays.asList(
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(1),
                            InterpreterTestingTests.giveExampleTimestamp(4)
                        ),
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(2),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(3)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(1),
                            InterpreterTestingTests.giveExampleTimestamp(3)
                        )
                    )),
                    null,
                    null
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to generate report BUT there is no current data set")
        void attemptGenerateReport(){
            String expectedExceptionMsg = "Current data set empty; query the API first before generating report.";

            try { this.runWithInput("2\n"); }
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
        @DisplayName("Attempt to generate report BUT there is only one entry in the current data set (edge case)")
        void attemptGenerateReport2(){
            String expected = "Enter command (type '7' for help): \n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 2.5 with quartiles (Q1, Q2, and Q3) of 2.5 Mw, 2.5 Mw, and 2.5 Mw.\n" +
                "> Timing - monthly frequency of 0.00 earthquakes per month; mean intermission time is not applicable due to only having one filtered earthquake entry in report.\n" +
                "> Location - centroid at 12.562° N 61.750° E.\n" +
                "> Depth - mean of 8.00 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at 12.562° N, 61.750° E, with magnitude strength of 2.5 Mw hitting 8.00 Km deep, between 1970-01-21T02:08:20.400Z[UTC] and 1970-01-21T02:08:20.400Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 1.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n";

            String printed = "";
            try {
                printed = this.runWithInput(
                    "2\n",
                    "currentDataSet",
                    new EarthquakeEntry[]{
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(5)
                    },
                    InterpreterTestingTests.giveExampleTimestamp(1),
                    InterpreterTestingTests.giveExampleTimestamp(5)
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to generate report and there is only 2 entries in the current data set (normal case sernario)")
        void attemptGenerateReport3() {
            String expected = "Enter command (type '7' for help): \n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 3.8 with quartiles (Q1, Q2, and Q3) of 2.5 Mw, 3.8 Mw, and 5.1 Mw.\n" +
                "> Timing - mean intermission time of 39.0 hours with a monthly frequency of 0.00 earthquakes per month.\n" +
                "> Location - centroid at -38.719° N 120.875° E.\n" +
                "> Depth - mean of 404.00 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at -38.719° N, 120.875° E, with magnitude strength of 3.8 Mw hitting 404.00 Km deep, between 1970-01-21T02:08:20.400Z[UTC] and 1970-01-22T17:08:20.400Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n";

            String printed = "";
            try {
                printed = this.runWithInput(
                    "2\n",
                    "currentDataSet",
                    new EarthquakeEntry[]{
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(5),
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(2)
                    },
                    InterpreterTestingTests.giveExampleTimestamp(1),
                    InterpreterTestingTests.giveExampleTimestamp(4)
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Successfully submit API query with edge values")
        void submitApiQuery() {
            String expected = "Enter command (type '7' for help): \n" +
                "Enter the start timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): \n" +
                "Enter the end timestamp (format 'YYYY-MM-DD:HH' in UTC timezone): \n" +
                "Enter the limit of earthquake entries to retrieve (0-20000): \n" +
                "Enter the latitude of the center point of area (-90 to 90): \n" +
                "Enter the longitude of the center point of area (-180 to 180): \n" +
                "Enter the maximum radius from center point to retrieve earthquake entries (in kilometers, 0 to 20000): \n" +
                "Enter the minimum magnitude of earthquake entries to retrieve (-5.0 to 10.0): \n" +
                "Enter the maximum magnitude of earthquake entries to retrieve (-5.0 to 10.0): \n" +
                "Enter the minimum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): \n" +
                "Enter the maximum depth of earthquake entries to retrieve (in kilometers, greater than 0 to 800): \n" +
                "Query Submitted, responded, recorded, converted, and stored successfully.\n";

            String printed = "";
            try { printed = this.runWithInput("1\n1900-01-01:00\n2020-01-01:00\n10\n0\n0\n2000\n5\n10\n1\n800"); }
            //^ Query args includes some edge values.
            //^ Due to fetching and filtering 2000 entries, expect test to take a small while.
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e.getCause()); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to print all reports BUT there are no reports cached/stored")
        void attemptExportAllReports(){
            String expectedExceptionMsg = "No cached reports to export; generate report first before exporting.";

            try { this.runWithInput("4\n"); }
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
                "> Magnitude - mean of 7.5 with quartiles (Q1, Q2, and Q3) of 5.0 Mw, 7.5 Mw, and 10.0 Mw.\n" +
                "> Timing - mean intermission time of 306.0 hours with a monthly frequency of 0.00 earthquakes per month.\n" +
                "> Location - centroid at 12.562° N 55.000° E.\n" +
                "> Depth - mean of 8.00 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at 12.562° N, 55.000° E, with magnitude strength of 7.5 Mw hitting 8.00 Km deep, between 1970-01-21T07:10:37.200Z[UTC] and 1970-02-03T01:10:37.200Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at Thursday, January 8, 1970 12:24 UTC with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in Principality of Mars (at 5.123° N 99.999° E) happened at Wednesday, January 21, 1970 07:10 UTC with a magnitude of 10.0Mw hitting a depth: 1.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n";

            String printed = "";
            try {
                printed = this.runWithReports(
                    "4\n",
                    "reports",
                    new EarthquakeReport[]{
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                        },
                        InterpreterTestingTests.giveExampleTimestamp(1),
                        InterpreterTestingTests.giveExampleTimestamp(4)
                        ),
                    },
                    InterpreterTestingTests.giveExampleTimestamp(1),
                    InterpreterTestingTests.giveExampleTimestamp(4)
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Attempt to print all reports and there are 2 reports cached/stored")
        void attemptExportAllReports3(){
            String expected = "Enter command (type '7' for help): \n" +
                "--- Report #1 ---\n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 7.6 with quartiles (Q1, Q2, and Q3) of 5.1 Mw, 7.6 Mw, and 10.0 Mw.\n" +
                "> Timing - mean intermission time of 44.0 hours with a monthly frequency of 0.01 earthquakes per month.\n" +
                "> Location - centroid at -42.439° N 140.000° E.\n" +
                "> Depth - mean of 400.50 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at -42.439° N, 140.000° E, with magnitude strength of 7.6 Mw hitting 400.50 Km deep, between 1970-01-21T07:10:37.200Z[UTC] and 1970-01-23T03:10:37.200Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in New York (at -90.000° N 180.000° E) happened at Monday, January 19, 1970 10:41 UTC with a magnitude of 5.1Mw hitting a depth: 800.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in Principality of Mars (at 5.123° N 99.999° E) happened at Wednesday, January 21, 1970 07:10 UTC with a magnitude of 10.0Mw hitting a depth: 1.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "--- Report #2 ---\n" +
                "Earthquake report:\n" +
                "> Magnitude - mean of 6.3 with quartiles (Q1, Q2, and Q3) of 2.5 Mw, 6.3 Mw, and 10.0 Mw.\n" +
                "> Timing - mean intermission time of 5.0 hours with a monthly frequency of -0.29 earthquakes per month.\n" +
                "> Location - centroid at 8.843° N 80.875° E.\n" +
                "> Depth - mean of 4.50 km.\n" +
                "> Predicted next earthquake occurrence (based on given data set) - estimated to occur at 8.843° N, 80.875° E, with magnitude strength of 6.3 Mw hitting 4.50 Km deep, between 1970-01-21T01:10:37.200Z[UTC] and 1970-01-21T12:10:37.200Z[UTC].\n" +
                "> Total number of earthquake entries analysed - 2.\n" +
                "End of report.\n" +
                "Disclaimer - seeing the raw data set (all earthquake entries) is a separate action.\n" +
                "\n" +
                "### Raw Data Set: ###\n" +
                "Earthquake Data Set:\n" +
                "> Index #0 earthquake entry: earthquake in Principality of Mars (at 5.123° N 99.999° E) happened at Wednesday, January 21, 1970 07:10 UTC with a magnitude of 10.0Mw hitting a depth: 1.00 km)]\n" +
                "> Index #1 earthquake entry: earthquake in Random Island (at 12.562° N 61.750° E) happened at Wednesday, January 21, 1970 02:08 UTC with a magnitude of 2.5Mw hitting a depth: 8.00 km)]\n" +
                "\n" +
                "### ############# ###\n" +
                "--- --------------- ---\n" +
                "ALL REPORTS (and their respective raw data sets) EXPORTED SUCCESSFULLY TO CONSOLE\n";

            String printed = "";
            try {
                printed = this.runWithReports(
                    "4\n",
                    "reports",
                    new EarthquakeReport[]{
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(2),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(1),
                            InterpreterTestingTests.giveExampleTimestamp(3)
                        ),
                        EarthquakeReport.of(
                            new EarthquakeEntry[]{
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(4),
                                InterpreterTestingTests.giveExampleValidEarthquakeEntry(5)
                            },
                            InterpreterTestingTests.giveExampleTimestamp(4),
                            InterpreterTestingTests.giveExampleTimestamp(5)
                        ),
                    },
                    InterpreterTestingTests.giveExampleTimestamp(1),
                    InterpreterTestingTests.giveExampleTimestamp(5)
                );
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

            try { this.runWithInput("3\n"); }
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
        void attemptPrintRawData2(){
            String expected = "Enter command (type '7' for help): \n" +
                "Current data set is empty; no earthquake entries retrieved from last query.";

            String printed = "";
            try { printed = this.runWithInput("3\n", "currentDataSet", new EarthquakeEntry[0], null, null); }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }

        @Test
        @DisplayName("Print raw data with empty data set BUT the API query only returned one result (edge case)")
        void attemptPrintRawData3(){
            String expected = "Enter command (type '7' for help): \n" +
                "### Raw Data Set: ###\n" +
                "> #1 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at Thursday, January 8, 1970 12:24 UTC with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "### ############# ###\n";

            String printed = "";
            try {
                printed = this.runWithInput(
                    "3\n",
                    "currentDataSet",
                    new EarthquakeEntry[]{
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(1)
                    },
                    //: Timestamps don't matter here (not called in test run) thus are not specified.
                    null,
                    null
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
        @Test
        @DisplayName("Print raw data with empty data set and the API query returned two results (normal case)")
        void attemptPrintRawData4(){
            String expected = "Enter command (type '7' for help): \n" +
                "### Raw Data Set: ###\n" +
                "> #1 earthquake entry: earthquake in Some random ahh place (at 20.000° N 10.000° E) happened at Thursday, January 8, 1970 12:24 UTC with a magnitude of 5.0Mw hitting a depth: 15.00 km)]\n" +
                "> #2 earthquake entry: earthquake in New York (at -90.000° N 180.000° E) happened at Monday, January 19, 1970 10:41 UTC with a magnitude of 5.1Mw hitting a depth: 800.00 km)]\n" +
                "### ############# ###\n";

            String printed = "";
            try {
                printed = this.runWithInput(
                    "3\n",
                    "currentDataSet",
                    new EarthquakeEntry[]{
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                        InterpreterTestingTests.giveExampleValidEarthquakeEntry(2)
                    },
                    null,
                    null
                );
            }
            catch (Exception e) { Assertions.fail("Exception thrown during test: " + e); }
            //^ Satisfy compiler by handling forwarded unchecked exceptions.

            Assertions.assertEquals(expected.replace("\r\n", "\n").trim(), printed.replace("\r\n", "\n").trim());
        }
    }

    /**
     * Provides a valid example EarthquakeEntry instance based on the given example number.
     * <p>
     * Used for rapid prototyping of test cases.
     * @param exampleNumber Corresponding example number (1-5).
     * @return An earthquake entry.
     */
    static EarthquakeEntry giveExampleValidEarthquakeEntry(int exampleNumber) {
        //* Returns an example EarthquakeEntry instance based on the given example number.
        return switch (exampleNumber) {
            case 1 -> new EarthquakeEntry(5.0, "Some random ahh place", InterpreterTestingTests.giveExampleTimestampUnix(1), 10.0, 20.0, 15.0);
            case 2 -> new EarthquakeEntry(5.1, "New York", InterpreterTestingTests.giveExampleTimestampUnix(2), 180.0, -90.0, 800);
            case 3 -> new EarthquakeEntry(-5.0, "United States of England", InterpreterTestingTests.giveExampleTimestampUnix(3), 23.5, 20.0, 15.0);
            case 4 -> new EarthquakeEntry(10.0, "Principality of Mars", InterpreterTestingTests.giveExampleTimestampUnix(4), 99.999, 5.123, 1.0);
            case 5 -> new EarthquakeEntry(2.5, "Random Island", InterpreterTestingTests.giveExampleTimestampUnix(5), 61.75, 12.562, 8.0);
            default -> throw new IllegalArgumentException("Invalid example number: " + exampleNumber);
        };
    }
    /**
     * Provides a valid example ZonedDateTime instance based on the given example number.
     * <p>
     * Used for rapid prototyping of test cases.
     * @param exampleNumber Corresponding example number (1-5).
     * @return A UTC date time timestamp.
     */
    static ZonedDateTime giveExampleTimestamp(int exampleNumber) {
        return switch (exampleNumber) {
            case 1 -> ZonedDateTime.of(1990, 8, 1, 1, 0, 0, 0, ZoneId.of("UTC"));
            case 2 -> ZonedDateTime.of(2020, 7, 2, 12, 0, 0, 0, ZoneId.of("UTC"));
            case 3 -> ZonedDateTime.of(2021, 4, 1, 2, 0, 0, 0, ZoneId.of("UTC"));
            case 4 -> ZonedDateTime.of(2025, 7, 30, 1, 0, 0, 0, ZoneId.of("UTC"));
            case 5 -> ZonedDateTime.of(2025, 1, 1, 3, 0, 0, 0, ZoneId.of("UTC"));
            default -> throw new IllegalArgumentException("Invalid example number: " + exampleNumber);
        };
    }
    /**
     * Provides a valid example Unix timestamp string based on the given example number.
     * <p>
     * Used for rapid prototyping of test cases.
     * @param exampleNumber Corresponding example number (1-5).
     * @return Unix epoch timestamp.
     */
    static long giveExampleTimestampUnix(int exampleNumber) {
        return InterpreterTestingTests.giveExampleTimestamp(exampleNumber).toEpochSecond();
    }
    /* Everytime I write up syntax, that calls this method, every static member in this file gets error saying "Compact source files are not supported at language level '22'"
    static EarthquakeReport giveExampleValidEarthquakeReport(int exampleNumber) {

        return switch (exampleNumber) {
            case 1 -> EarthquakeReport.of(
                new EarthquakeEntry[]{
                    tests.InterpreterTestingTests.giveExampleValidEarthquakeEntry(1),
                    tests.InterpreterTestingTests.giveExampleValidEarthquakeEntry(2)
                },
                tests.InterpreterTestingTests.giveExampleTimestamp(1),
                tests.InterpreterTestingTests.giveExampleTimestamp(3)
            );
            case 2 -> EarthquakeReport.of(
                new EarthquakeEntry[]{
                    tests.InterpreterTestingTests.giveExampleValidEarthquakeEntry(3),
                    tests.InterpreterTestingTests.giveExampleValidEarthquakeEntry(4)
                },
                tests.InterpreterTestingTests.giveExampleTimestamp(2),
                tests.InterpreterTestingTests.giveExampleTimestamp(5)
            );
            case 3 -> EarthquakeReport.of(
                new EarthquakeEntry[]{
                    tests.InterpreterTestingTests.giveExampleValidEarthquakeEntry(5)
                },
                tests.InterpreterTestingTests.giveExampleTimestamp(5),
                tests.InterpreterTestingTests.giveExampleTimestamp(5)
            );
            default -> throw new IllegalArgumentException("Invalid example number: " + exampleNumber);
        };
    }
    */
    /**
     * Sets up the console input and output streams for testing.
     * @param consoleInput The predefined console inputs to be used (each input separated by "\n").
     */
    static void setIO(String consoleInput) {
        System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));
        //^ Set console input to our predefined input.

        //: Set up to capture console output.
        InterpreterTestingTests.output = System.out;
        InterpreterTestingTests.outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
}