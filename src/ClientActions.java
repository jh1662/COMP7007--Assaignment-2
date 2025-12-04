public interface ClientActions {
    //: Relating to current query
    void submitQuery();
    void generateReport();

    //: Relating to history of reports
    void exportAllReports();
    void compareToPreviousReport();

    //: Relating to program operation
    void exitProgram();
    void getManual();
}
