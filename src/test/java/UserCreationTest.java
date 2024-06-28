import config.Messages;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserCreationTest extends AbstractTest{

    private Steps step = new Steps();
    private User newUser;

    @Before
    public void setUp(){
        newUser = new User();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        newUser.setPassword(RandomStringUtils.randomAlphabetic(5));
        newUser.setName(RandomStringUtils.randomAlphanumeric(10));
    }

    @Test
    public void userCreationTest(){
        Response response = step.postUserCreation(newUser);
        step.checkCreation(response, newUser.getEmail(), newUser.getName());

    }

    @Test
    public void userAlreadyExistTest(){
        step.postUserCreation(newUser);
        Response response = step.postUserCreation(newUser);
        step.badRequest(response, false, Messages.ERROR_EXISTED_USER, 403);
    }

    @After
    public void tearDown(){
        String token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
    }

}
