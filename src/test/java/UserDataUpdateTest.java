import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

public class UserDataUpdateTest {
    HTTPClient httpClient = new HTTPClient();
    User user;
    private String token;
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
    @DisplayName("Тест метода обновления всех данных пользователя")
    @Description("При обновлении всех дынных пользователя, при условии использования нового " +
            "уникального email возвращается код 200 и значение ключа <success> - <true>")
    public void userAllDataUpdateWithAuthTest(){
        httpClient
                .doUserDataUpdateRequest(token, "q"+user.getEmail(),
                        "q"+user.getPassword(), "q"+user.getName())
                .then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }
    @Test
    @DisplayName("Тест метода обновления email пользователя с авторизацией")
    @Description("При обновлении email пользователя, при условии использования нового " +
            "уникального email возвращается код 200 и значение ключа <success> - <true>")
    public void userEmailDataUpdateWithAuthTest(){
        httpClient
                .doUserDataUpdateRequest(token, "q"+user.getEmail(),
                        user.getPassword(), user.getName())
                .then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Тест метода обновления password пользователя с авторизацией")
    @Description("При обновлении password пользователя, " +
            "возвращается код 200 и значение ключа <success> - <true>")
    public void userPasswordDataUpdateWithAuthTest(){
        httpClient
                .doUserDataUpdateRequest(token, user.getEmail(),
                        "q"+user.getPassword(), user.getName())
                .then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }
    @Test
    @DisplayName("Тест метода обновления name пользователя с авторизацией")
    @Description("При обновлении name пользователя, " +
            "возвращается код 200 и значение ключа <success> - <true>")
    public void userNameDataUpdateWithAuthTest(){
        httpClient
                .doUserDataUpdateRequest(token, user.getEmail(),
                        user.getPassword(), "q"+user.getName())
                .then().assertThat().statusCode(200).and().body("success", equalTo(true));
    }
    @Test
    @DisplayName("Тест метода обновления email пользователя с авторизацией, новый email уже используется")
    @Description("При обновлении email пользователя, при использовании уже занятого email " +
            "возвращается код 403 и значение ключа <success> - <false>, " +
            "сообщение об ошибке <message> - <User with such email already exists>")
    public void userEmailDataUpdateWithAuthWithExistEmailTest() throws InterruptedException{
        User user2 = new User();
        user2.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user2);
                response.then().assertThat().statusCode(200);
        String token2= httpClient.extractToken(response);
        TimeUnit.SECONDS.sleep(1); //avoiding 429
        httpClient
                .doUserDataUpdateRequest(token, user2.getEmail(),
                        user.getPassword(), user.getName())
                .then().assertThat().statusCode(403).and().body("success", equalTo(false))
                .and().body("message", equalTo("User with such email already exists"));

        httpClient.doDeleteUserRequest(token2).then().assertThat().statusCode(202); //удаление пользователя, занимающего "новый email"
    }
    @Test
    @DisplayName("Тест метода обновления данных пользователя без авторизации")
    @Description("При обновлении данных пользователя, без авторизации " +
            "возвращается код 401 и значение ключа <success> - <false>, " +
            "сообщение об ошибке <message> - <You should be authorised>")
    public void userDataUpdateWithOutAuthTest(){
        httpClient
                .doUserDataUpdateRequest("", "q"+user.getEmail(),
                        "q"+user.getPassword(), "q"+user.getName())
                .then().assertThat().statusCode(401).and().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
    }
}
