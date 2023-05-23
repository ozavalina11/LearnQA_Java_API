package tests;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Epic("Тесты на DELETE")
    @Test
    @DisplayName("Попытка удаления пользователя по ID 2")
    public void Ex18deleteUserWithId2() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @DisplayName("Удаление пользователя с проверкой")
    public void Ex18deleteUser() {
        //{"id":"71275"} delete5@example.com
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "delete5@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/71275", header, cookie);

        Assertions.assertResponseTextEquals(responseDeleteUser, "");

        Response responseUserDataAfterDelete = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/71275", header, cookie);

        Assertions.assertResponseTextEquals(responseUserDataAfterDelete, "User not found");
    }

    @Test
    @DisplayName("Попытка удаления пользователя из-под другого пользователя")
    public void Ex18deleteUserByAnotherUser() {
        //{"id":"71286"} delete10@example.com
        //{"id":"71293"} delete11@example.com
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "delete10@example.com");
        authData.put("password", "123");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestWithoutTokenAndCookie("https://playground.learnqa.ru/api/user/71293");

        Assertions.assertResponseTextEquals(responseDeleteUser, "Auth token not supplied");
    }
}
