import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPRetriever {
    //! API doc at - https://earthquake.usgs.gov/fdsnws/event/1/

    //: Singleton pattern implementation.
    static final private HTTPRetriever instance = new HTTPRetriever();
    static public HTTPRetriever getInstance() { return HTTPRetriever.instance; }

    //! No constructor to enforce singleton pattern.

    public JsonObject requestAPIDataRecord(APIQuery query) throws IOException {
        URL uRL;
        try { uRL = new URL(query.toString()); }
        catch (MalformedURLException e) { throw new MalformedURLException(e.getMessage()); }

        JsonObject responseData;
        try {
            HttpURLConnection hTTPConnection = this.sendToServer(uRL);
            responseData = this.fetchJSON(hTTPConnection);
        }
        catch (IOException e) { throw new IOException(e.getMessage()); }

        return responseData;
    }

    private JsonObject fetchJSON(HttpURLConnection hTTPConnection) throws IOException {
        //: Store response data from HTTP response as string using buffers - one line at a time.
        StringBuilder response = new StringBuilder();
        String inputLine;
        try {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(hTTPConnection.getInputStream()));
            while ((inputLine = bufferReader.readLine()) != null) { response.append(inputLine); }
            bufferReader.close();
        }
        catch (IOException e) { throw new IOException("Failed to read data from HTTP response stream."); }

        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }

    private HttpURLConnection sendToServer(URL query) throws IOException {
        HttpURLConnection hTTPConnection;
        try {
            hTTPConnection = (HttpURLConnection) query.openConnection();
            //^ Set up HTTP connection to the USGS api server.
            //^ According to '.openConnection()' doc, it only sets up connection object, not actually connecting (over network) yet.
            hTTPConnection.setRequestMethod("GET");
            //^ Setting request method to GET to inform server what we want to retrieving data.
        }
        catch (IOException e) { throw new IOException("Failed to open HTTP connection to USGS api."); }

        boolean successfulConnection = false;
        int responseHTTPCode = 0;
        //^ Initialised to satisfy compiler; will be assigned actual response code upon successful connection.
        for (int i = 0; i < 5; i++) {
            //* Allow 5 attempts to establish network connection and get response code from server.
            try { responseHTTPCode = hTTPConnection.getResponseCode(); }
            //^ Establish connection over network and get response code from server.
            //^ We expect '200 OK' response code for successful GET request;
            //^ Anything other code indicates failure of some sort such as 4xx client-side errors and 5xx server-side errors.
            catch (IOException e) {
                //* Very probable point of failure - failing to establish network connection.
                //* Not worth failing the entire request due to transient network issues.
                System.out.println("Attempt #" + (i+1) + " to establish network connection (to API) has failed. Retrying...");
                continue;
            }
            successfulConnection = true;
            break;
        }
        if (!successfulConnection) throw new IOException("Failed to establish network connection to USGS api after 5 attempts.");
        //^ If fails after 5 attempts, assume network issues.

        if (responseHTTPCode != HttpURLConnection.HTTP_OK) throw new ConnectException(String.format("GET request failed. Response Code - %s.", responseHTTPCode));
        //^ Checks if sent request was successful or in vien.
        //^ If response code is not 200 - 200 is the only successful response code for GET requests.
        //^ 'ConnectException' is child of 'IOException'.

        return hTTPConnection;
    }

}
