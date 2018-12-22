import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class TesterHomeTest {

    @BeforeClass
    public static void beforeAll(){
        useRelaxedHTTPSValidation();
    }
    @Test
    public void topics(){
        get("https://testerhome.com/api/v3/topics.json")
                .then()
                .body("topics[0].title", containsString("上海沙龙"));
    }

    @Test
    public void getDemo(){
        given()
                .log().all()
                .proxy(8080)
                .param("wd", "mp3")
                .param("ie", "utf-8")
                .cookie("PSTM", "1510600412")
                .cookie("BIDUPSID", "85614512151C6A21725938906A7419A2")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                .get("https://www.baidu.com/s")
        .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void postDemo(){
        given()
                .proxy(8080)
                .formParam("j_username", "abc")
                .formParam("j_password", "123")
                .formParam("from", "/")
                .formParam("Submit", "Sign in")
        .when()
                .post("http://jenkins.testing-studio.com:8080/j_acegi_security_check")
        .then()
                .statusCode(302);
    }


}
