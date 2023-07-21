package tests;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

public class RestAssuredTests {
    public static String TOKEN_VALUE;
    public static final String TOKEN = "token";
    private Response bookingsById = RestAssured.given().log().all().get("https://restful-booker.herokuapp.com/booking");
    private String changeName = "Andriy";
    private String changeAddInfo = "Dinner";
    private int priceTest = 400;

    @BeforeTest
    public void setup(){
        RestAssured.baseURI="https://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .build();
    }

    @Test
    public void createBooking(){
        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname("Victor")
                .lastname("Koval")
                .totalprice(300)
                .depositpaid(true)
                .bookingdates(BookingDateBody.builder().checkin("2018-01-01").checkout("2018-11-11").build())
                .additionalneeds("some annotations")
                .build();

        Response response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .contentType(ContentType.JSON)
                .body(body)
                .post("/booking");
        response.prettyPrint();
    }

    @Test
    public void getBookingIds(){
        Response response = RestAssured.given().log().all().get("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
    }

    @Test
    public void updateBookingPrice() {
        bookingsById.then().statusCode(200);
        int bookingId = bookingsById.jsonPath().get("bookingid.find{it<100}");
        JSONObject body = new JSONObject();
        body.put("totalprice", priceTest);
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .cookie(TOKEN, TOKEN_VALUE)
                .queryParam("id",bookingId)
                .body(body.toString())
                .patch("/booking/"+bookingId);
        response.prettyPrint();
        response.then().statusCode(200);
        response.then().assertThat().body("totalprice", equalTo(priceTest));
    }

    @Test
    public void updateBookingNameAdd() {
        bookingsById.then().statusCode(200);
        int bookingId = bookingsById.jsonPath().get("bookingid.find{it>100}");
        Response response = RestAssured.get("/booking/" + bookingId);
        String responseBody = response.getBody().asString();
        JSONObject bookingInfo = new JSONObject(responseBody);
        JSONObject bookingDates = bookingInfo.getJSONObject("bookingdates");
        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname(changeName)
                .lastname(bookingInfo.getString("lastname"))
                .totalprice(bookingInfo.getInt("totalprice"))
                .depositpaid(bookingInfo.getBoolean("depositpaid"))
                .bookingdates(BookingDateBody.builder()
                        .checkin(bookingDates.getString("checkin"))
                        .checkout(bookingDates.getString("checkout"))
                        .build())
                .additionalneeds(changeAddInfo)
                .build();

        Response updatedBooking = RestAssured.given()
                .body(body)
                .put("/booking/" + bookingId);
        updatedBooking.prettyPrint();
        updatedBooking.then().statusCode(200);
        updatedBooking.then().assertThat().body("firstname", equalTo(changeName));
        updatedBooking.then().assertThat().body("additionalneeds", equalTo(changeAddInfo));
    }

    @Test
    public void deleteBookingById(){
        bookingsById.then().statusCode(200);
        int bookingId = bookingsById.jsonPath().get("bookingid.find{it>1000}");
        int testBookingId = bookingId;
        Response response = RestAssured.given().log().all()
                .cookie(TOKEN, TOKEN_VALUE)
                .queryParam("id",bookingId)
                .delete("/booking/"+bookingId);
        response.prettyPrint();
        response.then().statusCode(201);
        response.then().assertThat().body(not(containsString("\"id\": " + testBookingId)));
    }
}