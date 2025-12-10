import org.junit.jupiter.api.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

class RecordRetrieverTests {

    @BeforeEach
    void setUp() {
        HTTPRetriever retriever = new HTTPRetriever();
    }

    @AfterEach
    void tearDown() {
    }

}