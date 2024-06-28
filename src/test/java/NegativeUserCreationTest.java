import config.Messages;
import dto.User;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NegativeUserCreationTest extends  AbstractTest{

    Steps step = new Steps();
    private User brokenUser;
    private String email;

    private String password;

    private String name;
    private boolean success;
    private String message;
    private int code;

    public NegativeUserCreationTest(String email, String password, String name, boolean success, String message, int code){
        this.email = email;
        this.password = password;
        this.name = name;
        this.success = success;
        this.message = message;
        this.code = code;
    }

    @Parameterized.Parameters
    public static Object[][] getCourierData() {
        return new Object[][]{
                {"", RandomStringUtils.randomAlphabetic(15),  RandomStringUtils.randomAlphabetic(15), false, Messages.ERORR_FILED, 403},
                {RandomStringUtils.randomAlphabetic(10) + "@yandex.ru", "",  RandomStringUtils.randomAlphabetic(15), false, Messages.ERORR_FILED, 403},
                {RandomStringUtils.randomAlphabetic(10) + "@yandex.ru", RandomStringUtils.randomAlphabetic(15),  "", false, Messages.ERORR_FILED, 403},
        };
    }

    @Test
    public void noMandatoryFieldTest(){
        brokenUser = new User(email, password, name);
        Response response = step.postUserCreation(brokenUser);
        step.badRequest(response, success, message, code);
    }

    @After
    public void tearDown(){
        String token = step.getToken(new User(brokenUser.getEmail(), brokenUser.getPassword()));
        if (token != null){
            step.deleteUser(token).then().statusCode(202);
        }
    }
}
