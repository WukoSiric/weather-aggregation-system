import org.json.*;
import java.io.*;
import java.util.Map;

public class AggregationServerTest {
    private AggregationServer aggregationServer;
    private static final String TEST_JSON = "{\"id\": \"123\", \"temperature\": 25.0, \"humidity\": 50.0}";

    public AggregationServerTest() {
        aggregationServer = new AggregationServer();
    }

    public void runTests() {
        testIsValidPutRequest();
        testIsValidGetRequest();
        testExtractContentLength();
    }

    public void testIsValidPutRequest() {
        String validPutRequest = "PUT /weather.json HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 58\r\n" +
                "\r\n" +
                "{\"id\": \"123\", \"temperature\": 25.0, \"humidity\": 50.0}";

        if (AggregationServer.isValidPutRequest(validPutRequest)) {
            System.out.println("testIsValidPutRequest: Passed");
        } else {
            System.out.println("testIsValidPutRequest: Failed");
        }

        // Invalid request without Content-Type header
        String invalidPutRequest = "PUT /weather.json HTTP/1.1\r\n" +
                "Content-Length: 58\r\n" +
                "\r\n" +
                "{\"id\": \"123\", \"temperature\": 25.0, \"humidity\": 50.0}";

        if (!AggregationServer.isValidPutRequest(invalidPutRequest)) {
            System.out.println("testIsValidPutRequest (Invalid): Passed");
        } else {
            System.out.println("testIsValidPutRequest (Invalid): Failed");
        }
    }

    public void testIsValidGetRequest() {
        String validGetRequest = "GET /weather.json HTTP/1.1\n" +
                "\n";

        if (AggregationServer.isValidGetRequest(validGetRequest)) {
            System.out.println("testIsValidGetRequest: Passed");
        } else {
            System.out.println("testIsValidGetRequest: Failed");
        }

        // Invalid request with additional headers
        String invalidGetRequest = "GET /weather.json HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n";

        if (!AggregationServer.isValidGetRequest(invalidGetRequest)) {
            System.out.println("testIsValidGetRequest (Invalid): Passed");
        } else {
            System.out.println("testIsValidGetRequest (Invalid): Failed");
        }

        // Invalid request with different URI
        String invalidGetRequest2 = "GET /other.json HTTP/1.1\r\n" +
                "\r\n";

        if (!AggregationServer.isValidGetRequest(invalidGetRequest2)) {
            System.out.println("testIsValidGetRequest (Invalid 2): Passed");
        } else {
            System.out.println("testIsValidGetRequest (Invalid 2): Failed");
        }
    }

    public void testExtractContentLength() {
        String requestWithContentLength = "PUT /weather.json HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 58\r\n" +
                "\r\n" +
                "{\"id\": \"123\", \"temperature\": 25.0, \"humidity\": 50.0}";

        int contentLength = AggregationServer.extractContentLength(requestWithContentLength);

        if (contentLength == 58) {
            System.out.println("testExtractContentLength: Passed");
        } else {
            System.out.println("testExtractContentLength: Failed");
        }

        // Request without Content-Length header
        String requestWithoutContentLength = "PUT /weather.json HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                "{\"id\": \"123\", \"temperature\": 25.0, \"humidity\": 50.0}";

        int contentLength2 = AggregationServer.extractContentLength(requestWithoutContentLength);

        if (contentLength2 == -1) {
            System.out.println("testExtractContentLength (No Content-Length): Passed");
        } else {
            System.out.println("testExtractContentLength (No Content-Length): Failed");
        }
    }

    public static void main(String[] args) {
        String testOutputFilePath = "./Tests/test-AggregationServer.txt";
        AggregationServerTest test = new AggregationServerTest();
        try (PrintStream fileStream = new PrintStream(new FileOutputStream(testOutputFilePath))) {
            System.setOut(fileStream);
            test.runTests();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
