import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Homework {

    @Test
    public void testHello() {
        System.out.println("Hello from Olga");
    }

    @Test
    public void oneEx4() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.print();
    }

    @Test
    public void testEx5() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String message = response.get("messages[1].message");
        if (message == null) {
            System.out.println("Error");
        } else {
            System.out.println(message);
        }
    }

    @Test
    public void testEx6() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testEx7() {
        int statusCode = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int counter = 0;
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();
            statusCode = response.getStatusCode();
            if (statusCode != 200) {
                url = response.getHeader("Location");
                System.out.println(url);
                System.out.println("Код статуса " + statusCode);
                counter++;
            } else {
                System.out.println("Дошли до ответа с кодом статуса " + statusCode);
                System.out.println("Количество редиректов: " + counter);
            }
        }
        while (statusCode == 301);
    }

    @Test
    public void testEx8() { //throws InterruptedException
        // 1. Создается задача
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        JsonPath response = RestAssured
                .get(url)
                .jsonPath();

        Integer seconds = response.get("seconds");
        String token = response.get("token");
        System.out.println("Задача создана с token: " + token + "\nЗадача будет готова через: " + seconds + "сек");

        // 2. Выполняется один запрос с token ДО того, как задача готова, проверяется поле status
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        response = RestAssured
                .given()
                .queryParams(params)
                .get(url)
                .jsonPath();
        String error = response.get("error");
        if (error == null) {
            String status = response.get("status");
            if (status.equals("Job is NOT ready")) {

                // 3. Ожидание на seconds*1000 мс
                try {
                    sleep(seconds * 1000);
                } catch (Exception e) {
                    System.out.println(e);
                }

                // 4. Выполнение запроса c token ПОСЛЕ того, как задача готова, проверка поля status и наличие поля result
                response = RestAssured
                        .given()
                        .queryParams(params)
                        .get(url)
                        .jsonPath();
                status = response.get("status");
                String result = response.get("result");
                if (result != null && status.equals("Job is ready")) {
                    System.out.println(status);
                    System.out.println("Результат = " + result);
                } else System.out.println(status);
            } else System.out.println(status);
        } else System.out.println("No job linked to this token");
    }

    @Test
    public void testEx9() {
        String[] passwords = new String[]{"password", "123456", "123456789", "12345678", "12345", "123456789", "qwerty", "abc123", "football", "1234567", "monkey", "111111", "letmein", "1234", "1234567890", "dragon", "baseball", "sunshine", "iloveyou", "trustno1", "princess", "adobe123[a]", "123123", "welcome", "login", "admin", "qwerty123", "solo", "1q2w3e4r", "master", "666666", "photoshop[a]", "1qaz2wsx", "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey", "access", "flower", "555555", "passw0rd", "shadow", "lovely", "7777777", "michael", "!@#$%^&*", "jesus", "password1", "superman", "hello", "charlie", "888888", "696969", "qwertyuiop", "hottie", "freedom", "aa123456", "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1", "qazwsx", "Football", "000000", "123qwe"};
        Map<String, String> date = new HashMap<>();
        date.put("login", "super_admin");
        for (int i = 0; i < passwords.length; i++) {
            date.put("password", passwords[i]);

            Response response = RestAssured
                    .given()
                    .body(date)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = response.getCookie("auth_cookie");

            // передаем куки
            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            Response responseForCheck = RestAssured
                    .given()
                    .cookies(cookies)
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            if (!responseForCheck.asString().equals("You are NOT authorized")) {
                responseForCheck.print();
                System.out.println("Пароль: " + passwords[i]);
                break;
            }
        }
    }
}
