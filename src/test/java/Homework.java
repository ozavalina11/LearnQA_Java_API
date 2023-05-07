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
}
