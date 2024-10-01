package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters.UuidConverter;
import org.codehaus.groovy.classgen.asm.AssertionWriter;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.bytebuddy.build.Plugin.Factory.UsingReflection.Priority;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.restassured.RestAssured;
import java.io.File;
import java.util.UUID;
import org.testng.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



public class testCase_API_01 {

    RequestSpecification http;
    String email;
    String password;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
        RestAssured.basePath = "/api/v1/";
    }

    @Test (groups = "apiTests")
    public void RegisterAndLogin() {
        //RegisterAndLogin
        JSONObject obj = new JSONObject();
        email = "azhar" + UUID.randomUUID() + "@gmail.com";
        password = UUID.randomUUID().toString();

        obj.put("email", email);
        obj.put("password", password);
        obj.put("confirmpassword", password);
        System.out.println(obj.toString());

        // http = RestAssured.given().header("Content Type"
        // ,"application/json").body("{\"email\":\"azhar1989@gmail.com\",\"password\":\"password\",\"confirmpassword\":\"password\"}");
        http = RestAssured.given().log().all().header("Content-Type", "application/json")
                .body(obj.toString());
        Response resp = http.when().post("register");
        System.out.println(resp.asPrettyString());
        Assert.assertEquals(resp.getStatusCode(), 201);

        // JsonPath jp = new JsonPath(resp.getBody().toString());
        Assert.assertTrue(resp.getBody().jsonPath().get("success"));

        http = RestAssured.given().log().all().header("Content-Type", "application/json").body(obj.toString());
        Response resp2 = http.when().post("login");
        System.out.println(resp2.asPrettyString());
        Assert.assertEquals(resp2.getStatusCode(), 201);

        // JsonPath jp = new JsonPath(resp.getBody().toString());
        Assert.assertTrue(resp.getBody().jsonPath().get("success"));
       
    }
}

    /*@Test(priority = 1)
    public void login() {
        JSONObject obj = new JSONObject();
        obj.put("email", email);
        obj.put("password", password);
        System.out.println(obj.toString());

        http = RestAssured.given().log().all().header("Content-Type", "application/json")
                .body(obj.toString());
        Response resp = http.when().post("login");
        System.out.println(resp.asPrettyString());
        Assert.assertEquals(resp.getStatusCode(), 201);

        // JsonPath jp = new JsonPath(resp.getBody().toString());
        Assert.assertTrue(resp.getBody().jsonPath().get("success"));

        File fileobj = new File("src/test/resources/schema.json");
        JsonSchemaValidator matcher = JsonSchemaValidator.matchesJsonSchema(fileobj);
        resp.then().assertThat().body(matcher);

    }
}*/
