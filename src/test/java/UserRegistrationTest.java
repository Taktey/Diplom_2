import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

public class UserRegistrationTest {
    HTTPClient httpClient = new HTTPClient();
    private String token;

    @After
    public void deleteTestUser() throws InterruptedException{
        httpClient.doDeleteUserRequest(token).then().assertThat().statusCode(202);
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Тест регистрации пользователя")
    @Description("При попытке зарегистрировать пользователя с заполнением всех обязательных полей" +
            "возвращается код 200, значение ключа <success> - <true>")
    public void isUserRegistrationReturnsSuccessTrueAndCorrectStatusCode200Test() {
        User user = new User();
        user.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(200).and().body("success", equalTo(true));
        token = httpClient.extractToken(response);

    }

    @Test
    @DisplayName("Негативный тест регистрации пользователя, повторное использование email")
    @Description("При попытке зарегистрировать пользователя с использованием занятого email и с заполнением всех обязательных полей" +
            "возвращается код 403, значение ключа <success> - <false>")
    public void isDoubleUserRegistrationWithSameEmailReturnsSuccessFalseAndStatusCode403Test() throws InterruptedException{
        User user = new User();
        user.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(200);
        token = httpClient.extractToken(response);
        TimeUnit.SECONDS.sleep(1);
        response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(403).and().body("success", equalTo(false));
    }
}
