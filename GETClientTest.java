import java.io.*;
public class GETClientTest {
    public static void main(String[] args) {
        String testOutputFilePath = "./Tests/UNIT-Client.txt";
        try (PrintStream fileStream = new PrintStream(new FileOutputStream(testOutputFilePath))) {
            System.setOut(fileStream);
            testConstructGETRequestWithStationID();
            testConstructGETRequestWithoutStationID();
            testConstructGETRequestWithEmptyStationID();
            testConstructGETRequestWithSpecialCharacters();
            testConstructGETRequestWithLongStationID();
            testConstructGETRequestWithShortStationID();
            testConstructGETRequestWithNullStationID();
            testConstructGETRequestWithVeryLongEmptyStationID();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Test the constructGETRequest method with a station ID
    public static void testConstructGETRequestWithStationID() {
        String stationID = "123";
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\nStation-ID: 123\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(stationID);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithStationID");
    }

    // Test the constructGETRequest method without a station ID
    public static void testConstructGETRequestWithoutStationID() {
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(null);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithoutStationID");
    }

    // Test the constructGETRequest method with an empty station ID
    public static void testConstructGETRequestWithEmptyStationID() {
        String stationID = "";
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(stationID);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithEmptyStationID");
    }

    // Test the constructGETRequest method with special characters in the station ID
    public static void testConstructGETRequestWithSpecialCharacters() {
        String stationID = "!@#$%^&*()";
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\nStation-ID: !@#$%^&*()\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(stationID);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithSpecialCharacters");
    }

    // Test the constructGETRequest method with a long station ID
    public static void testConstructGETRequestWithLongStationID() {
        String stationID = "A";
        int repeatCount = 1000;
        StringBuilder stationIDBuilder = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            stationIDBuilder.append(stationID);
        }
        String longStationID = stationIDBuilder.toString();
        
        // Calculate the expected request length
        int expectedLength = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\n".length();
        if (!longStationID.isEmpty()) {
            expectedLength += "Station-ID: ".length() + longStationID.length() + "\r\n".length();
        }
        expectedLength += "\r\n".length();

        // Check that the request is correctly constructed and has the expected length
        String actualRequest = GETClient.constructGETRequest(longStationID);
        assertEqual(expectedLength, actualRequest.length(), "testConstructGETRequestWithLongStationID");
    }

    // Edge Case: Very Short Station ID
    public static void testConstructGETRequestWithShortStationID() {
        String stationID = "1";
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\nStation-ID: 1\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(stationID);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithShortStationID");
    }

    // Edge Case: Null Station ID
    public static void testConstructGETRequestWithNullStationID() {
        String stationID = null;
        String expectedRequest = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\n\r\n";
        String actualRequest = GETClient.constructGETRequest(stationID);
        assertEqual(expectedRequest, actualRequest, "testConstructGETRequestWithNullStationID");
    }

    // Edge Case: Very Long Station ID (Empty)
    public static void testConstructGETRequestWithVeryLongEmptyStationID() {
        int repeatCount = 1000;
        StringBuilder stationIDBuilder = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            stationIDBuilder.append("");
        }
        String longStationID = stationIDBuilder.toString();

        // Calculate the expected request length
        int expectedLength = "GET /weather.json HTTP/1.1\r\nUser-Agent: GETClient/1.0\r\n\r\n".length();

        // Check that the request is correctly constructed and has the expected length
        String actualRequest = GETClient.constructGETRequest(longStationID);
        assertEqual(expectedLength, actualRequest.length(), "testConstructGETRequestWithVeryLongEmptyStationID");
    }

    // Utility method to assert equality and print the result (same as before)
    private static void assertEqual(String expected, String actual, String testName) {
        if (expected.equals(actual)) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: \n" + expected);
            System.out.println("Actual: \n" + actual);
        }
    }

    // Utility method to assert equality of lengths and print the result (same as before)
    private static void assertEqual(int expected, int actual, String testName) {
        if (expected == actual) {
            System.out.println(testName + ": Passed");
        } else {
            System.out.println(testName + ": Failed");
            System.out.println("Expected: \n" + expected);
            System.out.println("Actual: \n" + actual);
        }
    }
}
