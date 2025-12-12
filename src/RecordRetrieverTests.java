import org.junit.jupiter.api.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.InputMismatchException;

class RecordRetrieverTests {
    private Interpreter interpreter = null;
    private final HTTPRetriever retriever = HTTPRetriever.getInstance();
    private final JSONToRecord converter = JSONToRecord.getInstance();

    @BeforeEach
    void setUp() {
        this.interpreter = new Interpreter();
    }

    @Test
    void test() throws Exception {
        try{
            interpreter.submitQuery();
            EarthquakeEntry[] rawData = this.converter.convert(this.retriever.requestAPIDataRecord(new APIQuery(
                20000,
                0,
                0,
                20000,
                ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0), ZonedDateTime.now().getZone()),
                ZonedDateTime.now(),
                -5.0,
                10.0,
                1,
                800
            )));
        }
        catch (Exception e){ throw new Exception(e.getMessage()); }
    }

    @AfterEach
    void tearDown() {

    }

}