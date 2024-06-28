import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.Before;

import static config.Configs.*;

public abstract class AbstractTest {
    @Before
    public void setUpRestAssured(){
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-type", "application/json")
                .setBaseUri(HOST)
                .build();
    }
}
