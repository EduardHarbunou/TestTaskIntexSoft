import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class MainTest extends BaseTest {
    private static final List<String> expectedBorders = new ArrayList<>(List.of("NOR", "FIN", "EST", "LVA", "LTU", "POL", "BLR",
            "UKR", "GEO", "AZE", "KAZ", "CHN", "MNG", "PRK", "JPN", "USA"));

    //
    @Test
    public void checkRussiaBordersList() throws ParseException {
        RequestSpecification requestSpecification = RestAssured.given().queryParam("codes", russianCountryCode);

        Response response = requestSpecification.get();

        isStatusCodeOk(response.statusCode());
        validateJsonSchema(response);

        String responseBody = response.getBody().asString();

        JSONArray jsonBody = (JSONArray) jsonParser.parse(responseBody);
        JSONObject russiaObject = (JSONObject) jsonBody.get(0);
        JSONArray arrayNeighboursRus = (JSONArray) russiaObject.get("borders");

        ArrayList<String> russianBordersFromResponse = new ArrayList<>();
        for (Object countryFromResponse : arrayNeighboursRus) {
            russianBordersFromResponse.add(countryFromResponse.toString());
        }

        for (String expectedCountry : expectedBorders) {
            softAssert.assertTrue(russianBordersFromResponse.contains(expectedCountry), "expected to see " + expectedCountry + " in the list\n");

            if (russianBordersFromResponse.contains(expectedCountry)) {
                System.out.println("List of borders contains: " + expectedCountry);
            }
        }
        softAssert.assertAll();
    }


    @Test
    public void checkCountriesFromBordersList() throws ParseException {
        RequestSpecification requestSpecification = RestAssured.given().queryParam("codes", russianCountryCode);
        Response response = requestSpecification.get();

        isStatusCodeOk(response.statusCode());
        validateJsonSchema(response);

        String bodyRus = response.getBody().asString();
        JSONArray jsonBody = (JSONArray) jsonParser.parse(bodyRus);
        JSONObject russiaObject = (JSONObject) jsonBody.get(0);
        JSONArray arrayNeighboursRus = (JSONArray) russiaObject.get("borders");

        ArrayList<String> russianBordersFromResponse = new ArrayList<>();
        for (Object countryFromResponse : arrayNeighboursRus) {
            russianBordersFromResponse.add(countryFromResponse.toString());
        }

        for (String country : russianBordersFromResponse) {
            Response neighbourResponse = RestAssured.given().queryParam("codes", country).get();
            System.out.println(country);
            isStatusCodeOk(neighbourResponse.statusCode());
            validateJsonSchema(neighbourResponse);

            String responseBody = neighbourResponse.getBody().asString();
            JSONArray neighbourResponseArray = (JSONArray) jsonParser.parse(responseBody);
            JSONObject neighbourObject = (JSONObject) neighbourResponseArray.get(0);
            JSONArray neighbourBordersArray = (JSONArray) neighbourObject.get("borders");

            ArrayList<String> neighbourBordersFromResponse = new ArrayList<>();
            for (Object countryFromResponse : neighbourBordersArray) {
                neighbourBordersFromResponse.add(countryFromResponse.toString());
            }

            softAssert.assertTrue(neighbourBordersFromResponse.contains(russianCountryCode));
            System.out.println(country + " has a border with Russia \n");
        }
    }

    private void validateJsonSchema(Response response) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath("schema.json"));
        System.out.println("JSON schema is valid");
    }

    private void isStatusCodeOk(int statusCode) {
        Assert.assertEquals(statusCode, 200, "Status code doesn't indicate success.");
        System.out.println("Status code is 200 OK");
    }
}
