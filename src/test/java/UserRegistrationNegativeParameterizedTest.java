import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UserRegistrationNegativeParameterizedTest {
    HTTPClient httpClient = new HTTPClient();
    private String email;
    private String password;
    private String name;
    private final int expectedStatusCode= 403;
    private final String expectedErrorMessage = "Email, password and name are required fields";

    public UserRegistrationNegativeParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Parameterized.Parameters(name="Тестовые данные: email: {0}, password: {1}, name: {2}")
    public static Object[][] getData(){
        return new Object[][]{
                {"testEmail@mail.ru", "testPassword",""},
                {"testEmail@mail.ru", "","testName"},
                {"", "testPassword","testName"},
        };
    }
    @Test
    @DisplayName("Негативный тест регистрации пользователя")
    @Description("При попытке зарегистрировать пользователя без заполнения всех обязательных полей" +
            "возвращается код 403, значение ключа <success> - <false>" +
            "и сообщение об ошибке <message> - <Email, password and name are required fields>")
    public void isRegistrationWithoutFieldFillingReturnsSuccessFalseAndExpectedStatusCodeAndExpectedErrorMessageTest()throws InterruptedException{
        User user = new User(email, password, name);
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(expectedStatusCode)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo(expectedErrorMessage));
        TimeUnit.SECONDS.sleep(1);
    }
}
