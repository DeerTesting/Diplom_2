import config.Messages;
import dto.Order;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Random;


public class ListOfOrdersTest extends AbstractTest {
    private Steps step = new Steps();
    private User newUser;

    @Before
    public void setUp(){
        newUser = new User();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        newUser.setPassword(RandomStringUtils.randomAlphabetic(5));
        newUser.setName(RandomStringUtils.randomAlphanumeric(10));

        step.postUserCreation(newUser);
    }

    @Test
    public void authorizedHasZeroOrderTest(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        Response response = step.getOrders(token);
        step.checkZeroOrders(response);

    }

    @Test
    public void authorizedHasMoreThanZeroOrdersTest(){

        List<String> ingredients = step.getIngredients();
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        Order order = new Order();
        step.createAnOrderList(ingredients, order, new Random());
        step.createAnOrder(order, token);

        Response response = step.getOrders(token);
        step.checkMoreThanZeroOrders(response);

    }

    @Test
    public void unauthorizedGetOrdersTest(){
        String token = "";
        Response response = step.getOrders(token);
        step.badRequest(response, false, Messages.AUTHORISATION_NECESSARY, 401);
    }


    @After
    public void tearDown(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
    }
}
