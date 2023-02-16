import io.restassured.RestAssured;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

public class BaseTest {
    SoftAssert softAssert = new SoftAssert();
    JSONParser jsonParser = new JSONParser();

    private static final String baseUrl = "https://restcountries.com/v2/alpha/";
    protected static final String russianCountryCode = "RUS";

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://restcountries.com/v2/alpha/";
    }
}
