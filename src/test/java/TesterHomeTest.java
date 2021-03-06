import io.restassured.RestAssured;
import io.restassured.builder.ResponseBuilder;
import io.restassured.config.SessionConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kohsuke.rngom.parse.host.Base;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.path.json.JsonPath.from;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;


public class TesterHomeTest {


    public static Filter decodeFilter=new Filter() {
        @Override
        public Response filter(FilterableRequestSpecification req, FilterableResponseSpecification res, FilterContext filterContext) {
            System.out.println("alter request");
            req.header("USER", "seveniruby");
            Response response=filterContext.next(req, res);

            Response responseNew=new ResponseBuilder().clone(response)
                    .setBody(
                            Base64.getDecoder().decode(response.body().asString().trim())
                    )
                    .setContentType(ContentType.JSON)
                    .build();
            System.out.println("alter response");
            return responseNew;
        }
    };

    public static SessionFilter sessionFilter=new SessionFilter();

    @BeforeClass
    public static void beforeAll() {
        useRelaxedHTTPSValidation();
        proxy(8080);
        System.out.println(filters().size());
        filters(sessionFilter);
        config = RestAssured.config().sessionConfig(new SessionConfig().sessionIdName("JSESSIONID.dd4a903c"));
    }

    @Test
    public void topics() {
        get("https://testerhome.com/api/v3/topics.json")
                .then()
                .body("topics[0].title", containsString("上海沙龙"));
    }

    @Test
    public void getDemo() {
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
    public void postDemo() {
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
    public void htmlPathDemo() {
        given()
                .log().all()
                //.proxy(8080)
                .param("wd", "mp3")
                .param("ie", "utf-8")
                .cookie("PSTM", "1510600412")
                .cookie("BIDUPSID", "85614512151C6A21725938906A7419A2")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                .get("http://www.baidu.com/s")
                .then()
                .log().all()
                .statusCode(200)
                .body("html.head.title", equalTo("mp3_百度搜索"))
                .body("**.find{it.@class=='nums_text'}", equalTo("百度为您找到相关结果约56,500,000个"))
        //百度做了升级，html不标准导致xml解析会报错，xpath就用不了了
        //.body(hasXPath("//*[@class='nums_text' and contains(text(), '百度为您找到相关结果约56,500,000个')]"))

        ;
    }

    @Test
    public void jsonPathDemo() {
        String title = "[北京沙龙] TesterHome 北京沙龙第 11 期，开始报名~";
        given().when().get("https://testerhome.com/api/v3/topics.json")
                .then()
                .statusCode(200)
                //not support
                //.body("topics..login[0]", equalTo("xxx"))
                .body("topics.title[0]", equalTo(title))
                .body("topics.size()", equalTo(23))
                .body("topics.find {it.title.contains('北京沙龙')}.title", equalTo(title))
                .body("topics.findAll {it.title.contains('北京沙龙')}.title[0]", equalTo(title))

        ;
    }

    @Test
    public void xmlPathDemo() {
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
    public void hamcrestDemo() {
        given()
                .when().get("http://jenkins.testing-studio.com:8080/job/AllureDemo/api/xml")
                .then()
                .statusCode(200)
                .body("..lastBuild.number.toFloat()", greaterThanOrEqualTo(16f))
                .body("..lastBuild.number.toInteger()", greaterThanOrEqualTo(16))
                .body("..lastBuild.number.toDouble()", closeTo(16, 2))
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
    public void xmlPostDemo() {
        DemoBean demoBean=new DemoBean();
        demoBean.setId(1);
        demoBean.setName("xxxxxxxx");
        given().log().all()
                .queryParam("access_token", "xxxxx")
                .contentType(ContentType.XML)
                .body(demoBean, ObjectMapperType.JAXB)
                .when()
                .post("https://qyapi.weixin.qq.com/cgi-bin/department/create")

        ;
    }


    @Test
    public void timeout() {
        given().log().all()
                .when().log().all().get("https://testerhome.com/api/v3/topics.json")
                .then().log().all()
                .statusCode(200)
                .time(lessThanOrEqualTo(2500L), TimeUnit.MILLISECONDS)
                .body("topics[0].title", containsString("沙龙"));
    }

    @Test
    public void schema() {
        given().when().get("https://testerhome.com/api/v3/topics/6040.json")
                .then()
                .body(matchesJsonSchema(new File("/tmp/2.json")));
    }

    @Test
    public void extract(){
        HashMap<String, Object> topic=given().log().all()
                .when().get("https://testerhome.com/api/v3/topics.json").prettyPeek()
                .then().log().all().statusCode(200)
                .extract().path("topics.find {it.title.contains(\"第五届\")}");
        System.out.println(topic);


        String login=given().log().all()
                .when().get("https://testerhome.com/api/v3/topics.json").prettyPeek()
                .then().log().all().statusCode(200)
                .extract().path("topics.find {it.title.contains(\"第五届\")}.user.login");
        System.out.println(login);


    }

    @Test
    public void extract2(){
        ValidatableResponse validatableResponse=given().log().all()
                .when().get("https://testerhome.com/api/v3/topics.json").prettyPeek()
                .then().log().all().statusCode(200);

        HashMap<String,Object> topic=validatableResponse.extract().path("topics.find {it.title.contains(\"第五届\")}");
        String login=validatableResponse.extract().path("topics.find {it.title.contains(\"第五届\")}.user.login");
        System.out.println(topic);
        System.out.println(login);
    }

    //最经常使用的方式
    @Test
    public void extract3(){
        Response response=given().log().all()
                .when().get("https://testerhome.com/api/v3/topics.json").prettyPeek()
                .then().log().all().statusCode(200)
                .extract().response();

        HashMap<String,Object> topic=response.path("topics.find {it.title.contains(\"第五届\")}");
        String login=response.path("topics.find {it.title.contains(\"第五届\")}.user.login");
        System.out.println(topic);
        System.out.println(login);
    }

    @Test
    public void httpbasic(){
        given().auth().basic("hogwarts", "123456")
        .when().get("http://jenkins.testing-studio.com:9001/").prettyPeek()
        .then().statusCode(200);
    }

    @Test
    public void auth2(){
        given().auth().oauth2("185f15c52b44bfc7103232ff1a98ed16480f57f1")
        .when().get("https://api.github.com/user/emails").prettyPeek()
        .then().statusCode(200);
    }


    @Test
    public void decode(){
        given().auth().basic("hogwarts", "123456")
        .when().get("http://jenkins.testing-studio.com:9001/base64.json")
        .then().statusCode(200).body("data.items.quote.name", equalTo("上证指数"));
    }

    @Test
    public void decode2(){
        String body=given().auth().basic("hogwarts", "123456")
                .when().get("http://jenkins.testing-studio.com:9001/base64.json")
                .then().statusCode(200)
                .extract().body().asString();
        System.out.println(body);

        String bodyDecode=new String(Base64.getDecoder().decode(body.trim()));
        System.out.println(bodyDecode);

        String name=from(bodyDecode).get("data.items.quote.name[0]");
        System.out.println(name);
        assertThat(name, equalTo("上证指数"));
    }

    @Test
    public void decodeByFilter(){
        given().log().all().proxy(8080)
                .filter(decodeFilter)
                .auth().basic("hogwarts", "123456")
                .when().log().all().get("http://jenkins.testing-studio.com:9001/base64.json")
                .then().statusCode(200).body("data.items.quote.name[0]", equalTo("上证指数"));
    }

    @Test
    public void jenkins(){
        RestAssured.proxy(8080);

        String cookie=given()
                .formParam("j_username", "hogwarts")
                .formParam("j_password", "hogwarts123456")
        .when().post("http://jenkins.testing-studio.com:8080/j_acegi_security_check")
        .then().statusCode(302)
        .extract().cookie("JSESSIONID.dd4a903c");
        System.out.println(cookie);


        given().cookie("JSESSIONID.dd4a903c", cookie)
        .when().log().all().get("http://jenkins.testing-studio.com:8080/")
        .then().log().all().statusCode(200);
    }

    @Test
    public void jenkinsBySessionFilter(){
        given()
                .filter(sessionFilter)
                .formParam("j_username", "hogwarts")
                .formParam("j_password", "hogwarts123456")
                .when().post("http://jenkins.testing-studio.com:8080/j_acegi_security_check")
                .then().statusCode(302);


        given().filter(sessionFilter)
                .when().log().all().get("http://jenkins.testing-studio.com:8080/")
                .then().log().all().statusCode(200);
    }

    @Test
    public void jenkinsByGlobalSessionFilter(){
        given()
                .formParam("j_username", "hogwarts")
                .formParam("j_password", "hogwarts123456")
                .when().post("http://jenkins.testing-studio.com:8080/j_acegi_security_check")
                .then().statusCode(302);


        given()
                .when().log().all().get("http://jenkins.testing-studio.com:8080/")
                .then().log().all().statusCode(200);
    }

}
