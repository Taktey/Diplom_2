import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    private String token;
    HTTPClient httpClient = new HTTPClient();
    User user;
    @Before
    public void createTestUser() throws InterruptedException{
        User user = new User();
        user.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(200);
        token= httpClient.extractToken(response);
        this.user = user;
        TimeUnit.SECONDS.sleep(1);
    }
    @After
    public void deleteTestUser(){
        httpClient.doDeleteUserRequest(token).then().assertThat().statusCode(202);
    }
    @Test
    @DisplayName("Тест метода входа в систему")
    @Description("При входе в систему с использованием валидных данных " +
            "возвращается код 200 и значение ключа <success> - <true>")
    public void userLoginPositiveTest(){
        httpClient.doUserLoginRequest(user).then().assertThat()
                .statusCode(200).and().body("success", equalTo(true));
    }
    @Test
    @DisplayName("Негативный тест метода входа в систему, неправильный email")
    @Description("При входе в систему с использованием неправильного email и существующего password " +
            "возвращается код 401 и значение ключа <success> - <false>, " +
            "сообщение об ошибке <message> - <email or password are incorrect>")
    public void userLoginNegativeWrongEmailTest(){
        user.setEmail(user.getEmail()+"1");
        httpClient.doUserLoginRequest(user).then().assertThat()
                .statusCode(401).and().body("success", equalTo(false))
                .and().body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Негативный тест метода входа в систему, неправильный password")
    @Description("При входе в систему с использованием неправильного password и существующего email " +
            "возвращается код 401 и значение ключа <success> - <false>, " +
            "сообщение об ошибке <message> - <email or password are incorrect>")
    public void userLoginNegativeWrongPasswordTest(){
        user.setPasseord(user.getPassword()+"1");
        httpClient.doUserLoginRequest(user).then().assertThat()
                .statusCode(401).and().body("success", equalTo(false))
                .and().body("message", equalTo("email or password are incorrect"));
    }

}
