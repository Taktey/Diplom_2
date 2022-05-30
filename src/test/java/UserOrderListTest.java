import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

public class UserOrderListTest {
    private String token;
    HTTPClient httpClient = new HTTPClient();
    private String realIngredientHash1="61c0c5a71d1f82001bdaaa6d";
    private String realIngredientHash2="61c0c5a71d1f82001bdaaa6f";
    List<String> ingredients= List.of(realIngredientHash1,realIngredientHash2);

    @Before
    public void createTestUser ()throws InterruptedException{
        User user = new User();
        user.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(200);
        token= httpClient.extractToken(response);
        TimeUnit.SECONDS.sleep(1);
        httpClient.doOrderCreationRequest(token, ingredients).then()
                .assertThat().statusCode(200);
        TimeUnit.SECONDS.sleep(1);
    }
    @After
    public void deleteTestUser() throws InterruptedException{
        httpClient.doDeleteUserRequest(token).then().assertThat().statusCode(202);
        TimeUnit.SECONDS.sleep(1);
    }
    @Test
    @DisplayName("Тест метода получения списка заказов пользователя с авторизацией")
    @Description("При авторизированном запросе списка заказов пользователя" +
            "возвращается код 200 и значение ключа <success> - <true>")
    public void userOrderListWithAuthTest(){
        Response response = httpClient.doGetUserOrderListRequest(token);
        response.then().assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }
    @Test
    @DisplayName("Негативный тест метода получения списка заказов пользователя без авторизаци")
    @Description("При неавторизированном запросе списка заказов пользователя" +
            "возвращается код 401 и значение ключа <success> - <false>" +
            "и сообщение об ошибке <message> - <You should be authorised>")
    public void userOrderListWithOutAuthTest(){
        Response response = httpClient.doGetUserOrderListRequest("");
        response.then().assertThat().statusCode(401)
                .and().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
    }
}
