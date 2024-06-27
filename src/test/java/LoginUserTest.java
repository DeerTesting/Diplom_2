import config.Messages;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LoginUserTest extends AbstractTest{
    private Steps step = new Steps();
    private User newUser;
    private User loginUser;

    @Before
    public void setUp(){
        newUser = new User();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        newUser.setPassword(RandomStringUtils.randomAlphabetic(5));
        newUser.setName(RandomStringUtils.randomAlphanumeric(10));

        loginUser = new User(newUser.getEmail(), newUser.getPassword());

        step.postUserCreation(newUser);
    }

    @Test
    public void successLogin(){
        Response response = step.loginUser(loginUser);
        step.checkLogin(response, newUser.getEmail(), newUser.getName());
    }

    @Test
    public void incorrectPassword(){
        loginUser.setPassword(newUser.getPassword()+RandomStringUtils.randomAlphabetic(5));

        Response response = step.loginUser(loginUser);
        step.badRequest(response, false, Messages.ERROR_WRONG_PASSWORD_EMAIL, 401);
    }

    @Test
    public void incorrectEmail(){
        loginUser.setEmail(RandomStringUtils.randomAlphabetic(5) + newUser.getEmail());

        Response response = step.loginUser(loginUser);
        step.badRequest(response, false, Messages.ERROR_WRONG_PASSWORD_EMAIL, 401);
    }

    @Test
    public void emptyPassword(){
        loginUser.setPassword("");

        Response response = step.loginUser(loginUser);
        step.badRequest(response, false, Messages.ERROR_WRONG_PASSWORD_EMAIL, 401);
    }

    @Test
    public void emptyEmail(){
        loginUser.setEmail("");

        Response response = step.loginUser(loginUser);
        step.badRequest(response, false, Messages.ERROR_WRONG_PASSWORD_EMAIL, 401);
    }

    @After
    public void tearDown(){
        String token = step.getToken(loginUser);
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
    }
}
