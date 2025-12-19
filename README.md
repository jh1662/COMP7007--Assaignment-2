# Earthquake Data Analysis Project - User Manual

To run the program, execute `Main.main` in `src/Main.Java`. Running can be achieved by right-clicking on `src/Main.Java` (in the IDEA IDE file explorer) and selecting "Run 'Main.main()'".

To run tests, execute the JUnit test cases in `InterpreterTesting.java` and `EarthquakeEntryConstructorTests.java`. Running can be achieved by right-clicking on the source `src/` folder (in the IDEA IDE file explorer) and selecting "Run 'All Tests'".

## Commands
| Command call | Command             | Description                                                                                                    |
|--------------|---------------------|----------------------------------------------------------------------------------------------------------------|
| `1`          | api query           | Make and commit data query to the USGS API.                                                                    |
| `2`          | generate report     | Generate report from current data set (from latest API query) and view it; said report will be saved in cache. |
| `3`          | view raw data set   | View the current data set (from latest API query) in raw format.                                               |
| `4`          | export all reports  | Export all cached reports (with their respective raw data) to console.                                         | 
| `5`          | compare to previous | Compare the latest report to the previous report (if both exist) and view the comparison.                      |
| `6`          | exit program        | Exit the program safely.                                                                                       |
| `7`          | help                | View this manual again.                                                                                        |

## Query arguments
When making an API query (command 1), the following parameters must be specified:

| Parameter     | Description                                                            | Example value | Restrictions                                                |
|---------------|------------------------------------------------------------------------|---------------|-------------------------------------------------------------|
| Start time    | Start time for the search period (in ISO format)                       | 2023-01-01:00 | Valid ISO 8601 datetime string and must be before end time  |
| End time      | End time for the search period (in ISO format)                         | 2023-01-31:23 | Valid ISO 8601 datetime string and must be after start time |
| Limit         | Maximum number of earthquake records to retrieve                       | 100           | Integer between 1 and 20000 unfiltered entries              |
| Latitude      | Latitude of the center point for the search area                       | 34.05         | Float between -90.0 and 90.0 degrees                        |
| Longitude     | Longitude of the center point for the search area                      | -118.25       | Float between -180.0 and 180.0 degrees                      |
| Radius        | Radius (in kilometers) from the center point to search for earthquakes | 50            | Integer between 1 and 20000 km                              |
| Min magnitude | Minimum magnitude of earthquakes to retrieve                           | 4.5           | Float between -5.0 and 10.0 and must be below max magnitude |
| Max magnitude | Maximum magnitude of earthquakes to retrieve                           | 10.0          | Float between -5.0 and 10.0 and must be above min magnitude |
| Min depth     | Minimum depth (in kilometers) of earthquakes to retrieve               | 1             | Float between 1.0 and 800.0 and must be below max depth     |
| Max depth     | Maximum depth (in kilometers) of earthquakes to retrieve               | 700           | Float between 1.0 and 800.0 and must be above max depth     |

## Specifying timestamps
| Component | Description                      | Example value | Restrictions                                        |
|-----------|----------------------------------|---------------|-----------------------------------------------------|
| Year      | Year component of the timestamp  | 2023          | Integer between 1900 and current year               |
| Month     | Month component of the timestamp | 01            | Integer between 01 and 12                           |
| Day       | Day component of the timestamp   | 15            | Integer between 01 and 31 (depending on leap years) |
| Hour      | Hour component of the timestamp  | 14            | Integer between 00 and 23                           |

Besides the individual components:
* Any inputted timestamp must be before current date and time.
* Timestamps precision is up to the hour; minutes, seconds, and smaller scales will not be specified.
* Timestamps must be specified in the format `YYYY-MM-DD:HH` (exactly 13 characters); for example timestamp as `2023-01-15:14`.
* Timestamps must in UTC timezone, as using other timezones will lead to unexpected behaviour; no need to specify timezone in the input.

## Filtering earthquake data
When collecting earthquake instances from the API response, the following filters are applied:
- Has valid (JSON) data structure.
- Magnitude type is Moment Magnitude (Mw) and its subtypes ('mwc', 'mww', etc.).
- User's specified query parameters (magnitude range, depth range, time, etc.).
This means that the limit specified in the query may not correspond to the actual number of earthquake instances retrieved - it is only the maximum number of records to fetch from the API response before filtering. The actual number of earthquake instances retrieved may be severely lower due to the applied filters. User is advised to set a higher limit if a reasonable data set is desired.

# Earthquake Data Analysis Project - Developer Documentation
- Refer to both Javadocs and inline code comments for detailed information about the codebase structure and implementation.
- History and reasoning of development decisions can be found in the project's `Development Log.docx`.
- JDK `22.0.1`, with the external libraries `com.google.code.gson:gson:2.13.1` and JUnit `org.junit.jupiter:junit-jupiter-api:5.12.2`, are dependencies to run this project.
- JUnits tests are in the `InterpreterTesting.java` and `EarthquakeEntryConstructorTests` - to run them
- Instead of Javadocs, JUnit test case documentation is shown in the `@DisplayName` annotations for each test case.

# Example use scenario
1. User starts the program by executing `Main.main`.
2. User inputs command `7` to view the user manual to know what to do.
3. User inputs command `1` to make an API query.
4. User inputs the following query parameters:
   - Start time: `1900-01-01:00`
   - End time: `2020-01-01:00`
   - Limit: `2000`
   - Latitude: `0`
   - Longitude: `0`
   - Radius: `20000`
   - Min magnitude: `-5`
   - Max magnitude: `10`
   - Min depth: `1`
   - Max depth: `800`
5. User waits, for up to a minute, for retrieving, filtering, and processing the fetched earthquake data.
6. User then inputs command `2` to generate a report regarding retrieved data set.
7. User gets more interested in the earthquake data, so user enters command `3` to view the raw data set.
8. User then inputs command `1` again to make another API query, this time with:
   - Start time: `2020-01-01:00`
   - End time: `2024-01-01:00`
   - Limit: `200`
   - Latitude: `20`
   - Longitude: `30`
   - Radius: `15000`
   - Min magnitude: `-3`
   - Max magnitude: `8`
   - Min depth: `1`
   - Max depth: `600`
9. User then inputs command `2` to generate a report regarding retrieved data set.
10. User then uses the second report to compare with the first report by inputting command `5`.