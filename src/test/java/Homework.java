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
}
