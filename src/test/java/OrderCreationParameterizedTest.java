import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(Parameterized.class)
public class OrderCreationParameterizedTest {
    private List<String> ingredients;
    private int expectedStatusCode;
    private HTTPClient httpClient = new HTTPClient();
    private String token;
    private String authorizationBreaker;
    private static String realIngredientHash1="61c0c5a71d1f82001bdaaa6d";
    private static String realIngredientHash2="61c0c5a71d1f82001bdaaa6f";

    public OrderCreationParameterizedTest(List<String> ingredients, String authorizationBreaker, int expectedStatusCode) {
        this.ingredients = ingredients;
        this.authorizationBreaker = authorizationBreaker;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters
    public static Object[][] getData(){
        return new Object[][]{
                {List.of(realIngredientHash1,realIngredientHash2),
                        "", 200}, // real hashes
                {List.of(realIngredientHash1,realIngredientHash2+"qwe"),
                        "",500}, // real+unreal
                {List.of(realIngredientHash1+"qwe",realIngredientHash2+"qwe"),
                        "",500}, // unreal hashes
                {List.of(""),"",500}, //no hashes
                {List.of(realIngredientHash1,realIngredientHash2),
                        "qwe",403}, // real hashes
                {List.of(realIngredientHash1,realIngredientHash2+"qwe"),
                        "qwe",403}, // real+unreal
                {List.of(realIngredientHash1+"qwe",realIngredientHash2+"qwe"),
                        "qwe",403}, // unreal hashes
                {List.of(""),"qwe",403}, //no hashes
        };
    }
    @Before
    public void createTestUser() throws InterruptedException{
        User user = new User();
        user.generateRandomUser();
        Response response = httpClient.doUserRegistrationRequest(user);
        response.then().assertThat().statusCode(200);
        token= httpClient.extractToken(response);
        long timeOut=1;
        TimeUnit.SECONDS.sleep(1); //avoiding 429
    }
    @After
    public void deleteTestUser() {
        httpClient.doDeleteUserRequest(token).then().assertThat().statusCode(202);
    }
    @Test
    @DisplayName("Тест метода создания заказа")
    @Description("Создание заказа авторизованным пользователем с использованием валидных хэшей ингредиента возвращает код 200" +
            "Если хотя бы один хэш ингредиента невалиден- возвращается код 500" +
            "При отсутствии авторизации возвращается код 403, валидность хэшей ингредиентов не проверяется")
    public void orderCreationTest(){
        Response response = httpClient.doOrderCreationRequest(token+authorizationBreaker, ingredients);
        response.then().assertThat().statusCode(expectedStatusCode);
    }
}

