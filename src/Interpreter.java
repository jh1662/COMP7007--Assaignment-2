import java.util.ArrayList;

public class Interpreter implements ClientActions {
    private final RecordRetriever recordRetriever = new RecordRetriever();
    private final ArrayList<EarthquakeReport> reports = new ArrayList<>();
    private EarthquakeEntry[] currentDataSet = null;

    public void interfacing() {

    }

    private boolean cycle() {
        return true;
    }

    private void processInputs(){

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
