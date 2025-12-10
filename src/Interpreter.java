import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter implements ClientActions {

    HTTPRetriever retriever = HTTPRetriever.getInstance();
    JSONToRecord converter = JSONToRecord.getInstance();

    private final ArrayList<EarthquakeReport> reports = new ArrayList<>();
    private EarthquakeEntry[] currentDataSet = null;

    public void interfacing() {

    }

    private boolean cycle() {
        return true;
    }

    private void processInputs(QueryParam expectedInput) {

    }


    @Override
    public void submitQuery() {

    }
    @Override
    public void generateReport() {

    }
    @Override
    public void viewRawDataSet() {

    }
    @Override
    public void exportAllReports() {

    }
    @Override
    public void compareToPreviousReport() {

    }
    @Override
    public void exitProgram() {

    }
    @Override
    public void getManual() {

    }
}
