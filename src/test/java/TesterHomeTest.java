import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class TesterHomeTest {
    @Test
    public void topics(){
        useRelaxedHTTPSValidation();
        get("https://testerhome.com/api/v3/topics.json")
                .then()
                .body("topics[0].title", containsString("上海沙龙"));
    }
}
