import org.json.*; 
import java.io.*;

public class ContentServerTest {
    public static void main(String[] args) {
        // Define the file path for the test output
        String testOutputFilePath = "./Tests/test-ContentServer.txt";

        try (PrintStream fileStream = new PrintStream(new FileOutputStream(testOutputFilePath))) {
            // Redirect the standard output to the test output file
            System.setOut(fileStream);

            // Run the tests
            testConstructPUTRequest();
            testCheckIfSuccessfulWithSuccessfulResponse();
            testCheckIfSuccessfulWithUnsuccessfulResponse();
            testReadFileWithValidFile();
            testReadFileWithInvalidFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Test the constructPUTRequest method
    public static void testConstructPUTRequest() {
        JSONObject json = new JSONObject("{\"key\": \"value\"}");
        ContentServer server = new ContentServer();
        String expectedRequest = "PUT /weather.json HTTP/1.1\r\n" +
                                "User-Agent: ATOMClient/1/0\r\n" +
                                "Content-Type: application/json\r\n" +
                                "Content-Length: 24\r\n" +
                                "\r\n" +
                                "{\r\n" +
                                "    \"key\": \"value\"\r\n" +
                                "}";
        String actualRequest = server.constructPUTRequest(json);
        assertEqual(expectedRequest, actualRequest, "testConstructPUTRequest");
    }

    // Test the checkIfSuccessful method with a successful response
    public static void testCheckIfSuccessfulWithSuccessfulResponse() {
        String response = "HTTP/1.1 200 OK";
        boolean isSuccess = ContentServer.checkIfSuccessful(response);
        assertEqual(true, isSuccess, "testCheckIfSuccessfulWithSuccessfulResponse");
    }

    // Test the checkIfSuccessful method with an unsuccessful response
    public static void testCheckIfSuccessfulWithUnsuccessfulResponse() {
        String response = "HTTP/1.1 404 Not Found";
        boolean isSuccess = ContentServer.checkIfSuccessful(response);
        assertEqual(false, isSuccess, "testCheckIfSuccessfulWithUnsuccessfulResponse");
    }

    // Test the readFile method with a valid file
    public static void testReadFileWithValidFile() {
        String validFilePath = "./Tests/valid.json"; // Provide a valid JSON file path here
        JSONObject json = ParseUtils.JSONObjectFromFile(validFilePath);
        assertNotNull(json, "testReadFileWithValidFile");
    }

    // Test the readFile method with an invalid file
    public static void testReadFileWithInvalidFile() {
        String invalidFilePath = "./Tests/invalid.json"; // Provide an invalid file path here
        JSONObject json = ParseUtils.JSONObjectFromFile(invalidFilePath);
        assertNull(json, "testReadFileWithInvalidFile");
    }

    // Utility method to assert equality and print the result
    private static void assertEqual(String expected, String actual, String testName) {
        if (expected.equals(actual)) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: \n" + expected);
            System.out.println("Actual: \n" + actual);
        }
    }

    // Utility method to assert boolean equality and print the result
    private static void assertEqual(boolean expected, boolean actual, String testName) {
        if (expected == actual) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
        }
    }

    // Utility method to assert that an object is not null and print the result
    private static void assertNotNull(Object object, String testName) {
        if (object != null) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: Not null");
            System.out.println("Actual: null");
        }
    }

    // Utility method to assert that an object is null and print the result
    private static void assertNull(Object object, String testName) {
        if (object == null) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: null");
            System.out.println("Actual: Not null");
        }
    }
}
