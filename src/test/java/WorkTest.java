import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class WorkTest {
    public static String token=null;
    @BeforeClass
    public static void beforeClass(){
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void getToken(){
        token=given()
                .param("corpid", "wwd6da61649bd66fea")
                .param("corpsecret", "8KEGhmql2Tj9stLV14mwCCR6POfphQpcOuqYQitrAMo")
        .when().get("https://qyapi.weixin.qq.com/cgi-bin/gettoken").prettyPeek()
        .then()
                .statusCode(200)
        .extract().path("access_token");

        System.out.println(token);

    }
}
