package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        Map<String, String> userData = DataGenerator.getInvalidRegistrationData();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseGetAuth, 400);
        Assertions.assertResponseTextEquals(responseGetAuth, "Invalid email format");
    }

    @Test
    public void testCreateUserWithShortUsername() {
        Map<String, String> userData = DataGenerator.getRegistrationDataWithShortUsername();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseGetAuth, 400);
        Assertions.assertResponseTextEquals(responseGetAuth, "The value of 'username' field is too short");
    }

    @Test
    public void testCreateUserWithLongUsername() {
        Map<String, String> userData = DataGenerator.getRegistrationDataWithLongUsername();

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseGetAuth, 400);
        Assertions.assertResponseTextEquals(responseGetAuth, "The value of 'username' field is too long");
    }

    @ParameterizedTest
    @CsvSource({", learnqa, learnqa, email, 123",
            "learnqa, , learnqa, email, 123",
            "learnqa, learnqa, , email, 123",
            "learnqa, learnqa, learnqa, , 123",
            "learnqa, learnqa, learnqa, email, ",
    })
    public void testCreateUserWithoutField(String username, String firstName, String lastName, String email, String password) {
        if (email != null) {
            email = DataGenerator.getRandomEmail() + "@example.com";
        }
        Map<String, String> userData = DataGenerator.getRegistrationData(username, firstName, lastName, email, password);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseGetAuth, 400);
        if (username == null)
            Assertions.assertResponseTextEquals(responseGetAuth, "The following required params are missed: username");
        if (email == null)
            Assertions.assertResponseTextEquals(responseGetAuth, "The following required params are missed: email");
        if (firstName == null)
            Assertions.assertResponseTextEquals(responseGetAuth, "The following required params are missed: firstName");
        if (lastName == null)
            Assertions.assertResponseTextEquals(responseGetAuth, "The following required params are missed: lastName");
        if (password == null)
            Assertions.assertResponseTextEquals(responseGetAuth, "The following required params are missed: password");
    }
}
