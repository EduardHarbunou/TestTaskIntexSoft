import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ResponseCodeTest extends BaseTest {

    @Test(dataProvider = "countryCodesAndStatusCodes")
    public void statusCodeTest(String countryCode, int expectedStatusCode, String message) throws ParseException {
        RequestSpecification requestSpecification = RestAssured.given().queryParam("codes", countryCode);

        Response response = requestSpecification.get();

        String body = response.getBody().asString();
        JSONObject jsonBody = (JSONObject) jsonParser.parse(body);
        String actualMessage = jsonBody.get("message").toString();

        int statusCode = response.getStatusCode();
        System.out.println("For country: " + countryCode + " status code is: " + statusCode);
        Assert.assertEquals(statusCode, expectedStatusCode);
        Assert.assertEquals(actualMessage, message);
    }

    @Test
    public void missedQueryParamTest() throws ParseException {
        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.get();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        JSONObject jsonBody = (JSONObject) jsonParser.parse(body);
        String message = jsonBody.get("message").toString();
        Assert.assertEquals(statusCode, 400);
        Assert.assertEquals(message, "Required argument [String codes] not specified");
    }

    @DataProvider
    public Object[][] countryCodesAndStatusCodes() {
        return new Object[][]{
                {"ABC", 404, "Not Found"},
                {"QWE", 404, "Not Found"},
                {"XYZ", 404, "Not Found"},
                {"GDR", 404, "Not Found"},
                {"YUG", 404, "Not Found"},
                {"URS", 404, "Not Found"},
                {"TCH", 404, "Not Found"},
                {"QWERT", 400, "Bad Request"},
                {"ASDF", 400, "Bad Request"},
                {"A", 400, "Bad Request"},
                {"Z", 400, "Bad Request"},
                {"", 400, "Bad Request"},
        };
    }
}
