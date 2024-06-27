import config.Messages;
import dto.Order;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderCreationTest extends AbstractTest{

    private Steps step = new Steps();
    Random randomGenerator = new Random();
    private User newUser;
    private Order order;

    List<String> ingredients;

    @Before
    public void setUp(){
        newUser = new User();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        newUser.setPassword(RandomStringUtils.randomAlphabetic(5));
        newUser.setName(RandomStringUtils.randomAlphanumeric(10));

        ingredients = step.getIngredients();
        step.postUserCreation(newUser);

        order = new Order();
    }

    @Test
    public void orderCreation(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));

        step.createAnOrderList(ingredients, order, randomGenerator);

        Response response = step.createAnOrder(order, token);
        step.checkOrderCreated(response, newUser, order);

    }

    @Test
    public void orderWrongIngredient(){

        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));

        order.setIngredients(new ArrayList<>(List.of(RandomStringUtils.randomAlphanumeric(10))));

        Response response = step.createAnOrder(order, token);
        step.wrongIngredient(response, 500);
    }

    @Test
    public void orderEmptyList(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));

        Response response = step.createAnOrder(order, token);
        step.badRequest(response, false, Messages.NO_INGREDIENTS, 400);
    }

    @Test
    public void noAuthorisationOrder(){
        String token = "";

        step.createAnOrderList(ingredients, order, randomGenerator);

        Response response = step.createAnOrder(order, token);
        step.checkNotLoggedInOrder(response);
    }

    @After
    public void tearDown(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
    }
}
