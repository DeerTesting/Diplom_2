import config.Messages;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EditUserTest extends AbstractTest{
    private Steps step = new Steps();
    private User newUser;
    private User filed;

    String token;
    String secondToken;

    @Before
    public void setUp(){
        newUser = new User();
        newUser.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        newUser.setPassword(RandomStringUtils.randomAlphabetic(5));
        newUser.setName(RandomStringUtils.randomAlphanumeric(10));
        step.postUserCreation(newUser);

        filed = new User();
        filed.setEmail(newUser.getEmail());
        filed.setName(newUser.getName());
    }

    @Test
    public void successEditTest(){
        token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        filed.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        filed.setName(RandomStringUtils.randomAlphabetic(10));
        Response response =  step.changeUser(token, "{\"name\": \"" + filed.getName() + "\", \"email\": \"" + filed.getEmail() + "\" }");
        step.checkChangeUser(response, filed);
    }

    @Test
    public void successEditNameTest(){
        token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        filed.setName(RandomStringUtils.randomAlphabetic(10));
        Response response =  step.changeUser(token, "{\"name\": \"" + filed.getName() + "\"}");
        step.checkChangeUser(response, filed);
    }

    @Test
    public void successEditEmailTest(){
        token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        filed.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");

        Response response =  step.changeUser(token, "{\"email\": \"" + filed.getEmail() + "\"}");
        step.checkChangeUser(response, filed);
    }

    @Test
    public void unloggedEditTest(){
        filed.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        filed.setName(RandomStringUtils.randomAlphabetic(10));
        token = "";

        Response response =  step.changeUser(token, "{\"name\": \"" + filed.getName() + "\", \"email\": \"" + filed.getEmail() + "\" }");
        step.badRequest(response, false, Messages.AUTHORISATION_NECESSARY, 401);
    }

    @Test
    public void existedEmail(){
        filed.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        filed.setName(RandomStringUtils.randomAlphabetic(10));
        filed.setPassword(newUser.getPassword());
        step.postUserCreation(filed);
        token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));

        Response response =  step.changeUser(token, "{\"name\": \"" + filed.getName() + "\", \"email\": \"" + filed.getEmail() + "\" }");
        step.badRequest(response, false, Messages.EMAIL_EXIST, 403);
    }

    @After
    public void tearDown(){
        token = step.getToken(new User(newUser.getEmail(), newUser.getPassword()));
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
        secondToken = step.getToken(new User(filed.getEmail(), newUser.getPassword()));
        if(secondToken != null){
            step.deleteUser(secondToken).then().statusCode(202);
        }
    }
}
