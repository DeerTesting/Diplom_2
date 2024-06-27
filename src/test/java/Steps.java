import dto.Order;
import dto.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Steps {

    @Step("Bad request")
    public void badRequest(Response response, boolean success, String message, int code){
        response.then()
                .body("success", equalTo(success))
                .body("message", equalTo(message))
                .and()
                .statusCode(code);
    }
    @Step("New user creation")
    public Response postUserCreation(User newUser){
        return given()
                .body(newUser)
                .when()
                .post(EndPoints.USER_CREATION);
    }

    @Step("New user creation success")
    public void checkCreation(Response response, String email, String name){
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Step("User login")
    public Response loginUser(User loginUser){
        return given()
                .body(loginUser)
                .when()
                .post(EndPoints.USER_LOGIN);
    }

    @Step("Get user token")
    public String getToken(User user){
        return  given()
                .body(user)
                .when()
                .post(EndPoints.USER_LOGIN)
                .then().extract().path("accessToken");
    }

    @Step
    public void checkLogin(Response response, String email, String name){
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name));
    }

    @Step("Change user data")
    public Response changeUser(String token, String field){
        return given()
                .header("Authorization", token)
                .body(field)
                .when()
                .patch(EndPoints.USER_DELETE_PATCH);
    }

    @Step("Patch body is correct")
    public void checkChangeUser(Response response, User newData){
        response.then()
                .statusCode(200)
                .and()
                .body("user.email", equalTo(newData.getEmail().toLowerCase()))
                .body("user.name", equalTo(newData.getName()));
    }

    @Step("User delete")
    public Response deleteUser(String token){
        return given()
                .header("Authorization",
                        token)
                .delete(EndPoints.USER_DELETE_PATCH);
    }

    @Step("Get user orders")
    public Response getOrders(String token){
        return given()
                .header("Authorization",
                        token)
                .get(EndPoints.GET_ORDERS);
    }

    @Step("Check success for user with 0 order")
    public void checkZeroOrders(Response response){
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("total", notNullValue())
                .body("totalToday", notNullValue())
                .body("orders", Matchers.hasSize(0));
    }

    @Step("Check success for user with more than 0 order")
    public void checkMoreThanZeroOrders(Response response){
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("total", notNullValue())
                .body("totalToday", notNullValue())
                .body("orders", notNullValue());
    }

    @Step("Create an order")
    public Response createAnOrder(Order order, String token){
        return given()
                .header("Authorization",
                        token)
                .body(order)
                .post(EndPoints.CREATE_ORDER);
    }

    @Step
    public void createAnOrderList(List<String> ingredients, Order order, Random randomGenerator){
        for (int i = 0; i <= randomGenerator.nextInt(ingredients.size()); i++){
            order.getIngredients().add(ingredients.get(randomGenerator.nextInt(ingredients.size())));
        }
    }

    @Step("successful order creation")
    public void checkOrderCreated(Response response, User user, Order order){
        response.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order._id", notNullValue())
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail().toLowerCase()))
                .body("order.createdAt", notNullValue())
                .body("order.updatedAt", notNullValue())
                .body("order.number", notNullValue())
                .body("order.price", notNullValue())
                .body("order.ingredients._id", equalTo(order.getIngredients()))
                .body("order.status", notNullValue());
    }

    @Step("Wrong ingredient")
    public void  wrongIngredient(Response response, int code){
        response.then()
                .statusCode(code);
    }

    @Step("Check not logged in order creation")
    public void checkNotLoggedInOrder(Response response){
        response.then()
                .statusCode(200)
                .and()
                .body("name", notNullValue())
                .body("order", notNullValue());
    }

    @Step("Getting ingredients id")
    public List<String> getIngredients(){
        return given()
                .get(EndPoints.GET_INGREDIENTS).then().extract().path("data._id");
    }
}
