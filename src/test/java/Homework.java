import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
            } else {
                System.out.println("Дошли до ответа с кодом статуса " + statusCode);
            }
        }
        while (statusCode == 301);
    }
}
