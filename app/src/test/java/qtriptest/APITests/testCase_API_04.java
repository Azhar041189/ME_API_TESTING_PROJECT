package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;


public class testCase_API_04 {
        RequestSpecification http;
        String email;
        String password;

        @BeforeClass
        public void setup() {
                RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
                RestAssured.basePath = "/api/v1/";
        }

        @Test (groups = "apiTests")
        public void registerNewUserAndRegisterDuplicateUser() {
                email = "testuser" + UUID.randomUUID() + "@gmail.com"; // Unique email
                password = "StrongPassword123"; // Sample password

                JSONObject obj = new JSONObject();
                obj.put("email", email);
                obj.put("password", password);
                obj.put("confirmpassword", password);

                Response response = RestAssured.given().contentType(ContentType.JSON)
                                .body(obj.toString()).post("register");

                Assert.assertEquals(response.getStatusCode(), 201,
                                "First registration should succeed.");
                Assert.assertTrue(response.jsonPath().getBoolean("success"),
                                "First registration should be successful.");


                Response response2 = RestAssured.given().contentType(ContentType.JSON)
                                .body(obj.toString()).post("register");

                Assert.assertEquals(response2.getStatusCode(), 400,
                                "Second registration should fail with 400 status.");
                Assert.assertFalse(response2.jsonPath().getBoolean("success"),
                                "Second registration should not be successful.");
                Assert.assertEquals(response2.jsonPath().getString("message"),
                                "Email already exists",
                                "Error message should indicate email is already registered.");
        }

        /*
         * @Test(priority = 1, dependsOnMethods = "registerNewUser") public void
         * registerDuplicateUser() { // Attempt to register with the same email JSONObject obj = new
         * JSONObject(); obj.put("email", email); // Using the same email obj.put("password",
         * password); obj.put("confirmpassword", password);
         * 
         * Response response =
         * RestAssured.given().contentType(ContentType.JSON).body(obj.toString()) .post("register");
         * 
         * Assert.assertEquals(response.getStatusCode(), 400,
         * "Second registration should fail with 400 status.");
         * Assert.assertFalse(response.jsonPath().getBoolean("success"),
         * "Second registration should not be successful.");
         * Assert.assertEquals(response.jsonPath().getString("message"), "Email already exists",
         * "Error message should indicate email is already registered.");
         * 
         * }
         */



}


