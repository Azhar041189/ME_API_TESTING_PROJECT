package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.openqa.selenium.json.Json;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;



public class testCase_API_03 {
    RequestSpecification http;
    String email;
    String password;
    String CityName;
    String token;
    String id;
    String adventureID;
    String bookingId;


    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
        RestAssured.basePath = "/api/v1/";
    }
    @Test (groups = "apiTests")
    public void RegisterAndLogin() {
        //RegisterAndLogin
        JSONObject obj = new JSONObject();
        email = "azharsh" + UUID.randomUUID() + "@gmail.com";
        password = UUID.randomUUID().toString();

        //Register
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

        // Login
        http = RestAssured.given().log().all().header("Content-Type", "application/json")
                .body(obj.toString());
        Response resp2 = http.when().post("login");
        System.out.println(resp2.asPrettyString());
        Assert.assertEquals(resp2.getStatusCode(), 201);

        // JsonPath jp = new JsonPath(resp.getBody().toString());
        Assert.assertTrue(resp2.getBody().jsonPath().get("success"));

        JsonPath jp = new JsonPath(resp2.getBody().asString());
        Assert.assertTrue(jp.getBoolean("success"));
        token = jp.get("data.token");
        id = jp.getString("data.id");

        RequestSpecification spec = RestAssured.given();
        Response resp3 = spec.when().get("cities?search=beng");

        List<LinkedHashMap<String, String>> list = resp3.body().jsonPath().getList("$");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("id").toString().contains("beng")) {
                CityName = list.get(i).get("id").toString();
                break;
            }
        }
        System.out.println(CityName);
        Assert.assertEquals(resp3.getStatusCode(), 200);
        System.out.println(resp3.getStatusCode());


        String description = list.get(0).get("description");
        System.out.println("City description: " + description);
        Assert.assertTrue(description.contains("100+ Places"),
                "Description does not contain '100+ Places'");

        CityName = list.get(0).get("id");
        System.out.println("City found with ID containing 'beng': " + CityName);
        Assert.assertTrue(CityName.contains("beng"), "City ID does not contain 'beng'");

        File fileobj = new File("src/test/resources/schema2.json");
        JsonSchemaValidator matcher = JsonSchemaValidator.matchesJsonSchema(fileobj);
        resp3.then().assertThat().body(matcher);

        Response resp4 = RestAssured.given().queryParam("city", CityName).get("adventures");
        List<LinkedHashMap<String, String>> listadv = resp4.body().jsonPath().getList("$");
        for (int i = 0; i < listadv.size(); i++) {
            if (listadv.get(i).get("name").toString().equals("Niaboytown")) {
                adventureID = listadv.get(i).get("id").toString();
                break;
            }
        }
        System.out.println(adventureID);

        // makereservation
        //JSONObject obj = new JSONObject();
        obj.put("userId", id);
        obj.put("name", id);
        obj.put("date", "2025-09-09");
        obj.put("person", "1");
        obj.put("adventure", adventureID);
        System.out.println(obj.toString());
        RequestSpecification http =
                RestAssured.given().log().all().header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token).body(obj.toString());
        Response resp5 = http.when().post("/reservations/new");
        System.out.println(resp5.body().asPrettyString());

        // Assert that the booking was successful
        Assert.assertEquals(resp5.getStatusCode(), 200, "Booking failed!");
        System.out.println(resp5.getStatusCode());


        //Verifying booking
        RequestSpecification getReservationsRequest =
                RestAssured.given().header("Authorization", "Bearer " + token);

        // Perform the GET request to fetch reservations
        Response getReservationsResponse =
                getReservationsRequest.queryParam("id", id).get("reservations");

        // Print the response for debugging
        System.out.println(getReservationsResponse.asPrettyString());

        // Assert that the GET reservations call was successful
        Assert.assertEquals(getReservationsResponse.getStatusCode(), 200,
                "Failed to fetch reservations!");

        // Parse the JSON response
        List<LinkedHashMap<String, Object>> reservations =
                getReservationsResponse.body().jsonPath().getList("$");

        boolean bookingFound = false;

        // Check if the user ID is present in the response
        for (LinkedHashMap<String, Object> reservation : reservations) {
            String responseUserId = (String) reservation.get("userId");
            System.out.println("Fetched User ID: " + responseUserId);

            if (responseUserId.equals(id)) {
                bookingFound = true;

                // Validate other reservation details (optional)
                // Assert.assertEquals(reservation.get("name"), id, "Name mismatch!");
                Assert.assertEquals(reservation.get("adventureName"), "Niaboytown",
                        "Adventure name mismatch!");
                Assert.assertEquals(reservation.get("date"), "2025-09-09", "Date mismatch!");

                System.out.println("Reservation found and verified for User ID: " + id);
                break;
            }
        }

        // Assert that the reservation for the user ID was found
        Assert.assertTrue(bookingFound, "No reservation found for the given User ID: " + id);
    }
   
}
