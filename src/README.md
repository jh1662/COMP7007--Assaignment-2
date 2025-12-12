# Earthquake Data Analysis Project

## Commands
| Command call | Command             | Description                                                                                                    |
|--------------|---------------------|----------------------------------------------------------------------------------------------------------------|
| 1            | api query           | Make and commit data query to the USGS API.                                                                    |
| 2            | generate report     | Generate report from current data set (from latest API query) and view it; said report will be saved in cache. |
| 3            | view raw data set   | View the current data set (from latest API query) in raw format.                                               |
| 4            | export all reports  | Export all cached reports (with their respective raw data) to console.                                         | 
| 5            | compare to previous | Compare the latest report to the previous report (if both exist) and view the comparison.                      |
| 6            | exit program        | Exit the program safely.                                                                                       |
| 7            | help                | View this manual again.                                                                                        |

## Query arguments
When making an API query (command 1), the following parameters must be specified:

| Parameter     | Description                                                            | Example value | Restrictions                   |
|---------------|------------------------------------------------------------------------|---------------|--------------------------------|
| Limit         | Maximum number of earthquake records to retrieve                       | 100           | Integer between 1 and 20000    |
| Latitude      | Latitude of the center point for the search area                       | 34.05         | Float between -90.0 and 90.0   |
| Longitude     | Longitude of the center point for the search area                      | -118.25       | Float between -180.0 and 180.0 |
| Radius        | Radius (in kilometers) from the center point to search for earthquakes | 50            | Integer between 1 and 20000    |
| Start time    | Start time for the search period (in ISO format)                       | 2023-01-01:00 | Valid ISO 8601 datetime string |
| End time      | End time for the search period (in ISO format)                         | 2023-01-31:23 | Valid ISO 8601 datetime string |
| Min magnitude | Minimum magnitude of earthquakes to retrieve                           | 4.5           | Float between 0.0 and 10.0     |
| Max magnitude | Maximum magnitude of earthquakes to retrieve                           | 10.0          | Float between 0.0 and 10.0     |
| Min depth     | Minimum depth (in kilometers) of earthquakes to retrieve               | 0             | Float between 0.0 and 700.0    |
| Max depth     | Maximum depth (in kilometers) of earthquakes to retrieve               | 700           | Float between 0.0 and 700.0    |

## Filtering earthquake data
When collecting earthquake instances from the API response, the following filters are applied:
- Has valid (JSON) data structure.
- Magnitude type is Moment Magnitude (Mw) and its subtypes ('mwc', 'mww', etc.).
- User's specified query parameters (magnitude range, depth range, time, etc.).