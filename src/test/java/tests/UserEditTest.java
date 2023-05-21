package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void Ex17TestEditDataByUnauthorizedUser() {
        //{"id":"71080"} unauthorized@example.com
        Response responseEditData = apiCoreRequests
                .makePutRequestWithoutTokenAndCookie("https://playground.learnqa.ru/api/user/71080", DataGenerator.getEditData());

        Assertions.assertResponseTextEquals(responseEditData, "Auth token not supplied");
    }

    @Test
    public void Ex17TestEditDataByAnotherAuthorizedUser() {
        //{"id":"71244"} editUserByAnotherUser@example.com
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "editUserByAnotherUser@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/71245", header, cookie, DataGenerator.getEditData());

        Assertions.assertResponseTextEquals(responseUserData, "");

        Response responseUserDataAfterEdit = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/71245", header, cookie);
        Assertions.assertJsonHasField(responseUserDataAfterEdit, "username");
        Assertions.assertResponseTextEquals(responseUserDataAfterEdit, "{\"username\":\"learnqa\"}");
        String[] expectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserDataAfterEdit, expectedFields);
    }

    @Test
    public void Ex17TestEditIncorrectEmail() {
        //{"id":"71245"}  editUserByAnother@example.com
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "editUserByAnother@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Map<String, String> editEmail = new HashMap<>();
        editEmail.put("email", "editUserByAnotherUserexample.com");

        Response responseUserData = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/71245", header, cookie, editEmail);

        Assertions.assertResponseTextEquals(responseUserData, "Invalid email format");
    }

    @Test
    public void Ex17TestEditFirstName() {
        //{"id":"71245"}  editUserByAnother@example.com
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "editUserByAnother@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Map<String, String> editEmail = new HashMap<>();
        editEmail.put("firstName", "1");

        Response responseUserData = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/71245", header, cookie, editEmail);

        Assertions.assertResponseTextEquals(responseUserData, "{\"error\":\"Too short value for field firstName\"}");
    }

    @Test
    public void Ex17_positiveForCheck() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "zavalinaO11@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditData = apiCoreRequests
                .makePutRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/71079", header, cookie, DataGenerator.getEditData());

        System.out.println("1" + responseEditData.asString());

        Response responseUserDataAfterEdit = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/71079", header, cookie);
        System.out.println("2" + responseUserDataAfterEdit.asString());
    }
}
