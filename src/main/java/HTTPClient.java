import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class HTTPClient {
    private final String JSON = "application/json";
    private final String baseURL = " https://stellarburgers.nomoreparties.site";
    private final String registrationAPIMethod = "/api/auth/register";
    private final String deleteUserAPIMethod = "/api/auth/user";
    private final String userLoginAPIMethod= "/api/auth/login";
    private final String userDataUpdateAPIMethod= "/api/auth/user";
    private final String orderCreationAPIMethod= "/api/orders";
    private final String userOrderListAPIMethod= "/api/orders";

    @Step("Send DELETE test user request")
    protected Response doDeleteUserRequest(String token){
        return given().auth().oauth2(token).header("Content-type", JSON).when().delete(baseURL + deleteUserAPIMethod);
    }
    @Step("GET user authentication")
    protected String extractToken(Response response){
        String responseBody=response.getBody().asString();
        JsonPath jp = new JsonPath(responseBody);
        return jp.getString("accessToken").split(" ")[1];
    }
    @Step("Send POST user registration request")
    protected Response doUserRegistrationRequest(User user){
        return given().header("Content-type", JSON).and().body(user).log().body().when().post(baseURL + registrationAPIMethod);
    }
    @Step("Send POST user login request")
    protected Response doUserLoginRequest(User user){
        String body= String.format("{%n\"email\": \"%s\",%n\"password\": \"%s\"%n}", user.getEmail(), user.getPassword());
        return given().header("Content-type", JSON).and().body(body).log().body().when().post(baseURL + userLoginAPIMethod);
    }
    @Step("Send PATCH update user data request")
    protected Response doUserDataUpdateRequest(String token, String newEmail, String newPassword, String newName){
        User user = new User(newEmail,newPassword,newName);
        return given().auth().oauth2(token).header("Content-type", JSON).and().body(user).when().patch(baseURL + userDataUpdateAPIMethod);
    }
    @Step("Send POST create order request")
    protected Response doOrderCreationRequest(String token, List<String> ingredients){
        Ingredients ingredientsList = new Ingredients(ingredients);
        return given().auth().oauth2(token).and().header("Content-type", JSON).and().body(ingredientsList).log().body().when().post(baseURL + orderCreationAPIMethod);
    }
    @Step("Send GET user order list request")
    protected Response doGetUserOrderListRequest(String token){
        return given().auth().oauth2(token).and().header("Content-type", JSON).and().when().get(baseURL + userOrderListAPIMethod);
    }
}
