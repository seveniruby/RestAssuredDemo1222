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
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                .header("Cookie", "PSTM=1510600412; BIDUPSID=85614512151C6A21725938906A7419A2; MCITY=-131%3A; BD_UPN=123253; BAIDUID=FB108BEDEF2809244BDCF4D3E2DAFBCE:FG=1; pgv_pvi=4063167488; delPer=0; BD_HOME=0; pgv_si=s3259169792; H_PS_PSSID=1444_21105_28131_27750_27244_27508; BD_CK_SAM=1; PSINO=2; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BDRCVFR[tox4WRQ4-Km]=mk3SLVN4HKm; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; H_PS_645EC=5ea2QBUj0CJPe2Nr3eydHojdzWAWLqpoRW04%2B9daio8tBpjhFBd4tJgOvCI; BDSVRTM=166")
                .get("https://www.baidu.com/s")
        .then()
                .log().all()
                .statusCode(200);
    }
}
