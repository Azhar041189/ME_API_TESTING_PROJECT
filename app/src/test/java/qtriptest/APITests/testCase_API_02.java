package qtriptest.APITests;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;

import java.io.File;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;


public class testCase_API_02 {
    RequestSpecification http;
    String CityName;
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
        RestAssured.basePath = "/api/v1/";
    }
    
    @Test (groups = "apiTests")
    public void getcities(){
        //getcities
       RequestSpecification  spec = RestAssured.given();
       Response resp = spec.when().get("cities?search=beng");

       List<LinkedHashMap<String, String>> list = resp.body().jsonPath().getList("$");
       for(int i=0; i<list.size(); i++){
                if(list.get(i).get("id").toString().contains("beng")) { 
                CityName = list.get(i).get("id").toString(); 
                break; 
                }
            }
        System.out.println(CityName);
        Assert.assertEquals(resp.getStatusCode(), 200);
        System.out.println(resp.getStatusCode()); 
       
        
        String description = list.get(0).get("description");
        System.out.println("City description: " + description);
        Assert.assertTrue(description.contains("100+ Places"), "Description does not contain '100+ Places'");

        CityName = list.get(0).get("id");
        System.out.println("City found with ID containing 'beng': " + CityName);
        Assert.assertTrue(CityName.contains("beng"), "City ID does not contain 'beng'");  

        File fileobj = new File("src/test/resources/schema2.json");
        JsonSchemaValidator matcher = JsonSchemaValidator.matchesJsonSchema(fileobj);
        resp.then().assertThat().body(matcher);
    }
}
