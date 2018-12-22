import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

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
                //.proxy(8080)
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

    @Test
    public void htmlPathDemo(){
        given()
                .log().all()
                //.proxy(8080)
                .param("wd", "mp3")
                .param("ie", "utf-8")
                .cookie("PSTM", "1510600412")
                .cookie("BIDUPSID", "85614512151C6A21725938906A7419A2")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                .get("https://www.baidu.com/s")
                .then()
                .log().all()
                .statusCode(200)
                .body("html.head.title", equalTo("mp3_百度搜索"))
                .body("**.find{it.@class=='nums_text'}", equalTo("百度为您找到相关结果约56,500,000个"))
                .body(hasXPath("//*[@class='nums_text' and contains(text(), '百度为您找到相关结果约56,500,000个')]"))

        ;
    }

    @Test
    public void jsonPathDemo(){
        String title = "[北京沙龙] TesterHome 北京沙龙第 11 期，开始报名~";
        given().when().get("https://testerhome.com/api/v3/topics.json")
        .then()
                .statusCode(200)
                .body("topics.title[0]", equalTo(title))
                .body("topics.size()", equalTo(23))
                .body("topics.find {it.title.contains('北京沙龙')}.title", equalTo(title))
                .body("topics.findAll {it.title.contains('北京沙龙')}.title[0]", equalTo(title))

        ;
    }

    @Test
    public void xmlPathDemo(){
        given()
                .when().get("http://jenkins.testing-studio.com:8080/job/AllureDemo/api/xml")
        .then()
                .statusCode(200)
                .body("freeStyleProject.displayName", equalTo("AllureDemo"))
                .body("..lastBuild.number", equalTo("16"))
                .body("..lastBuild.number.toFloat()", equalTo(16f))
                .body("**.find {it.name()=='lastSuccessfulBuild'}.number", equalTo("1"))
                .body("**.find {it.name()=='lastSuccessfulBuild'}.number.toInteger()", equalTo(1))
                .body("**.findAll {it.number=='1'}[-1].url", equalTo("http://jenkins.testing-studio.com:8080/job/AllureDemo/1/"))
        ;
    }

    @Test
    public void hamcrestDemo(){
        given()
                .when().get("http://jenkins.testing-studio.com:8080/job/AllureDemo/api/xml")
                .then()
                .statusCode(200)
                .body("..lastBuild.number.toFloat()", greaterThanOrEqualTo(16f))
                .body("..lastBuild.number.toInteger()", greaterThanOrEqualTo(16))
                .body("..lastBuild.number.toDouble()", closeTo(16,2))
                .body("**.find {it.name()=='lastSuccessfulBuild'}.number", equalTo("1"))
                .body("**.find {it.name()=='lastSuccessfulBuild'}.number.toInteger()", equalTo(1))
     ;
    }

    @Test
    public void jsonPost() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", "广州研发中心");
        data.put("parentid", 1);
        data.put("order", 1);
        data.put("id", 2);

        given().proxy(8080)
                .queryParam("access_token", "xxxxx")
                .contentType(ContentType.JSON)
                .body("{\"name\":\"北京研发中心\",\"id\":2,\"parentid\":1,\"order\":1}")
                .when()
                .post("https://qyapi.weixin.qq.com/cgi-bin/department/create")

        ;

        given().proxy(8080)
                .queryParam("access_token", "xxxxx")
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("https://qyapi.weixin.qq.com/cgi-bin/department/create")

        ;

    }
    @Test
            public void xmlPostDemo(){
        given().proxy(8080)
                .queryParam("access_token", "xxxxx")
                .contentType(ContentType.XML)
                .body(new DemoBean(), ObjectMapperType.JAXB)
                .when()
                .post("https://qyapi.weixin.qq.com/cgi-bin/department/create")

        ;
    }


    @Test
    public void timeout(){
        given().log().all()
        .when().log().all().get("https://testerhome.com/api/v3/topics.json")
        .then().log().all()
                .statusCode(200)
                .time(lessThanOrEqualTo(2500L), TimeUnit.MILLISECONDS)
                .body("topics[0].title", containsString("沙龙"));
    }

    @Test
    public void schema(){
        given().when().get("https://testerhome.com/api/v3/topics/6040.json")
        .then()
                .body(matchesJsonSchema(new File("/tmp/2.json")));
    }




}
