import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the EarthquakeEntry constructor.
 * <p>
 * Tests positive and negative cases for each field in the constructor; this includes valid edge cases, invalid edge cases, zero cases, and normal cases.
 * <p>
 * Table of test cases can be seen in the Javadoc of each nested class.
 * <p>
 * Each tested methods is done in its own nested class, to separate concerns and improve readability.
 * Meanwhile, each test case is a method within the nested class to allow immediate pinpointing of which exact test case, rather than the just group of tests, has failed.
 * <p>
 * Exceptions will not be caught here because JUnit can already handle "checked" exceptions.
 */
class EarthquakeEntryConstructorTests {
    //* Exceptions will not be caught here because JUnit can already handle "checked" exceptions.
    /**
     * Helper method to convert date and time components into a unix epoch timestamp string.
     * <p>
     * Used for rapid prototyping of test cases.
     * @param year Year component of the date.
     * @param month Month component of the date (1-12).
     * @param day Day component of the date (1-31).
     * @param hour Hour component of the time (0-23).
     * @return String representing the unix epoch timestamp in milliseconds.
     * <p>
     * table of test cases that ARE NOT inside nested classes:
     * <table border="1">
     *   <thead>
     *     <tr>
     *       <th>Method</th>
     *       <th>Description</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>createValidEarthquakeEntry</td>
     *       <td>Create valid EarthquakeEntry with normal values (not edge case, just an example)</td>:
     *     </tr>
     *     <tr>
     *       <td>createValidEarthquakeEntry2</td>
     *       <td>Create valid EarthquakeEntry with normal values where negative if possible (not edge case, just an example)</td>:
     *     </tr>
     *   </tbody>
     * </table>
     */
    static long toTimestamp(int year, int month, int day, int hour) {
        ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ZoneId.of("UTC"));
        return zdt.toInstant().toEpochMilli();
    }

    @Test
    @DisplayName("Create valid EarthquakeEntry with normal values (not edge case, just an example)")
    void createValidEarthquakeEntry() {
        new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
            //* Made as multiline for readability and rapid prototyping.
            EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
            10.0, 20.0, 15.0)
        ;
    }
    @Test
    @DisplayName("Create valid EarthquakeEntry with normal values where negative if possible (not edge case, just an example)")
    void createValidEarthquakeEntry2() {
        new EarthquakeEntry(-2.0, "Some random ahh place (on Earth)",
            //* Made as multiline for readability and rapid prototyping.
            EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
            -10.0, -20.0, 15.0)
        ;
    }

    /**
     * Valid edge case tests for EarthquakeEntry constructor - tests every field.
     * <p>
     * Test case table:
     * <table border="1">
     *     <thead>
     *       <tr>
     *         <th>Method</th>
     *         <th>Description</th>
     *       </tr>
     *     </thead>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry</td>
     *       <td>Create valid EarthquakeEntry with edge value - highest magnitude (10)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry2</td>
     *       <td>Create valid EarthquakeEntry with edge value - lowest magnitude (-5)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry3</td>
     *       <td>Create valid EarthquakeEntry with edge value - single character place name</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry4</td>
     *       <td>Create valid EarthquakeEntry with edge value - place name 512 characters long</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry5</td>
     *       <td>Create valid EarthquakeEntry with edge value - earliest valid timestamp (1900-01-01 12am UTC)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry6</td>
     *       <td>Create valid EarthquakeEntry with edge value - latest valid timestamp (current time)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry7</td>
     *       <td>Create valid EarthquakeEntry with edge value - lowest longitude (-180)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry8</td>
     *       <td>Create valid EarthquakeEntry with edge value - highest longitude (180)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry9</td>
     *       <td>Create valid EarthquakeEntry with edge value - lowest latitude (-90)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry10</td>
     *       <td>Create valid EarthquakeEntry with edge value - highest latitude (90)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry11</td>
     *       <td>Create valid EarthquakeEntry with edge value - lowest depth (1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry12</td>
     *       <td>Create valid EarthquakeEntry with edge value - highest depth (800)</td>
     *     </tr>
     */
    @Nested
    class ValidEdgeCases {
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - highest magnitude (10)")
        void createValidEdgeEarthquakeEntry() {
            new EarthquakeEntry(10.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - lowest magnitude (-5)")
        void createValidEdgeEarthquakeEntry2() {
            new EarthquakeEntry(-5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - single character place name")
        void createValidEdgeEarthquakeEntry3() {
            new EarthquakeEntry(5.0, "E",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - place name 512 characters long")
        void createValidEdgeEarthquakeEntry4() {
            new EarthquakeEntry(5.0, "E".repeat(512),
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - earliest valid timestamp (1900-01-01 12am UTC)")
        void createValidEdgeEarthquakeEntry5() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(1900, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - latest valid timestamp (current time)")
        void createValidEdgeEarthquakeEntry6() {
            ZonedDateTime currentTimestamp = ZonedDateTime.now(ZoneOffset.UTC);
            currentTimestamp = currentTimestamp.withMinute(0).withSecond(0).withNano(0);
            //^ Current time to the hour; user's input cannot be more precise than hour.
            long currentUnixEpoch = currentTimestamp.toInstant().toEpochMilli();

            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                currentUnixEpoch,
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - lowest longitude (-180)")
        void createValidEdgeEarthquakeEntry7() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                -180.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - highest longitude (180)")
        void createValidEdgeEarthquakeEntry8() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                180.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - lowest latitude (-90)")
        void createValidEdgeEarthquakeEntry9() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, -90.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - highest latitude (90)")
        void createValidEdgeEarthquakeEntry10() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 90.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - lowest depth (1)")
        void createValidEdgeEarthquakeEntry11() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 90.0, 1.0)
            ;
        }
        @Test
        @DisplayName("Create valid EarthquakeEntry with edge value - highest depth (800)")
        void createValidEdgeEarthquakeEntry12() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                //* Made as multiline for readability and rapid prototyping.
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 90.0, 800.0)
            ;
        }
    }

    /**
     * Invalid edge case tests for EarthquakeEntry constructor - tests every field.
     * <p>
     * Test cause table:
     * <table border="1">
     *   <thead>
     *     <tr>
     *       <th>Method</th>
     *       <th>Description</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too low magnitude (-5.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry2</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too high magnitude (10.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry3</td>
     *       <td>Create invalid EarthquakeEntry with edge value – empty string place name</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry4</td>
     *       <td>Create invalid EarthquakeEntry with edge value – place name 513 characters long</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry5</td>
     *       <td>Create invalid EarthquakeEntry with edge value – timestamp before 1900AD (1899‑12‑31 11pm UTC)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry6</td>
     *       <td>Create invalid EarthquakeEntry with edge value – timestamp after current date and time</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry7</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too low longitude (-180.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry8</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too high longitude (180.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry9</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too low latitude (-90.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry10</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too high latitude (90.1)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry11</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too low depth (0.0)</td>
     *     </tr>
     *     <tr>
     *       <td>createValidEdgeEarthquakeEntry12</td>
     *       <td>Create invalid EarthquakeEntry with edge value – too high depth (800.1)</td>
     *     </tr>
     *   </tbody>
     * </table>
     */
    @Nested
    class InvalidEdgeCases{
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value - too low magnitude (-5.1)")
        void createValidEdgeEarthquakeEntry() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(-5.1, "Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value - too high magnitude (10.1)")
        void createValidEdgeEarthquakeEntry2() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(10.1, "Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value - empty string place name")
        void createValidEdgeEarthquakeEntry3() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0, "",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  place name 513 characters long")
        void createValidEdgeEarthquakeEntry4() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0, "L".repeat(513),
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  timestamp before 1900AD (1899-12-31 11pm UTC)")
        void createValidEdgeEarthquakeEntry5() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(1899, 12, 31, 11),
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  timestamp after current date and time")
        void createValidEdgeEarthquakeEntry6() {
            ZonedDateTime futureTimestamp = ZonedDateTime.now(ZoneOffset.UTC);
            futureTimestamp = futureTimestamp.plusHours(2);
            //^ Add 2 hours, instead of one, in case current time increases by one hour unit during execution.
            long futureUnixEpoch = futureTimestamp.toInstant().toEpochMilli();

            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    futureUnixEpoch,
                    10.0, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too low longitude (-180.1)")
        void createValidEdgeEarthquakeEntry7() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    -180.1, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too high longitude (180.1)")
        void createValidEdgeEarthquakeEntry8() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    180.1, 20.0, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too low latitude (-90.1)")
        void createValidEdgeEarthquakeEntry9() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, -90.1, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too high latitude (90.1)")
        void createValidEdgeEarthquakeEntry10() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 90.1, 15.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too low depth (0.0)")
        void createValidEdgeEarthquakeEntry11() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 90.1, 0.0)
                ;
            });
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with edge value -  too high depth (800.1)")
        void createValidEdgeEarthquakeEntry12() {
            assertThrows(IllegalArgumentException.class, () -> {
                new EarthquakeEntry(5.0,"Some random ahh place (on Earth)",
                    //* Made as multiline for readability and rapid prototyping.
                    EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                    10.0, 90.1, 800.1)
                ;
            });
        }
    }

    /**
     * Valid zero value tests for EarthquakeEntry constructor - tests every field that can be zero.
     * <p>
     * Test case table:
     * <table border="1">
     *   <thead>
     *     <tr>
     *       <th>Method</th>
     *       <th>Description</th>
     *     </tr>
     *   </thead>
     *     <tbody>
     *       <tr>
     *         <td>createValidEdgeEarthquakeEntry</td>
     *         <td>Create invalid EarthquakeEntry with zero value - zero magnitude</td>
     *       </tr>
     *       <tr>
     *         <td>createValidEdgeEarthquakeEntry2</td>
     *         <td>Create invalid EarthquakeEntry with zero value - zero longitude</td>
     *       </tr>
     *       <tr>
     *         <td>createValidEdgeEarthquakeEntry3</td>
     *         <td>Create invalid EarthquakeEntry with zero value - zero latitude</td>
     *       </tr>
     *     </tbody>
     *   </table>
     */
    @Nested
    class ValidZeroCases {
        //! Unix epoch timestamps and depth cannot be zero.
        @Test
        @DisplayName("Create invalid EarthquakeEntry with zero value - zero magnitude")
        void createValidEdgeEarthquakeEntry() {
            new EarthquakeEntry(0, "Some random ahh place (on Earth)",
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with zero value - zero longitude")
        void createValidEdgeEarthquakeEntry2() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                0, 20.0, 15.0)
            ;
        }
        @Test
        @DisplayName("Create invalid EarthquakeEntry with zero value - zero latitude")
        void createValidEdgeEarthquakeEntry3() {
            new EarthquakeEntry(5.0, "Some random ahh place (on Earth)",
                EarthquakeEntryConstructorTests.toTimestamp(2000, 1, 1, 0),
                10.0, 0, 15.0)
            ;
        }
    }
}