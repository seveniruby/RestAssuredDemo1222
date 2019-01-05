import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class WorkTest {
    public static String token=null;
    @BeforeClass
    public static void beforeClass(){
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Before
    public void getToken() {
        token = given()
                .param("corpid", "wwd6da61649bd66fea")
                .param("corpsecret", "8KEGhmql2Tj9stLV14mwCCR6POfphQpcOuqYQitrAMo")
                .when().get("https://qyapi.weixin.qq.com/cgi-bin/gettoken").prettyPeek()
                .then()
                .statusCode(200)
                .extract().path("access_token");

        System.out.println(token);
    }

    @Test
    public void sendMsg(){
        HashMap<String, String> content=new HashMap<String, String>();
        content.put("content", "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况</a>，聪明避开排队。");
        HashMap<String, Object> msg=new HashMap<String, Object>();
        msg.put("touser", "sihan");
        msg.put("msgtype", "text");
        msg.put("agentid", "1000004");
        msg.put("text", content);

        given().queryParam("access_token", token).contentType(ContentType.JSON).body(msg)
        .when().post("https://qyapi.weixin.qq.com/cgi-bin/message/send").prettyPeek()
        .then().statusCode(200);

    }
}
