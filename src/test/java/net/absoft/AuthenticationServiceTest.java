package net.absoft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.listeners.RetryAnalyzer;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest {

  private AuthenticationService authenticationService;

  @BeforeMethod(groups = {"positive", "negative"})
  public void setUp() {
    authenticationService = new AuthenticationService();
    System.out.println("setup");
  }

  @Test(
          description = "Test Succesful Authentication",
          groups = "positive",
          retryAnalyzer = RetryAnalyzer.class
  )
  public void testSuccessfulAuthentication() {
    Response response = authenticationService.authenticate("user1@test.com", "password1");
    SoftAssert sf = new SoftAssert();
    sf.assertEquals(response.getCode(), 200, "Response code should be 200");
    sf.assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
    sf.assertAll();
  }

  @DataProvider(name = "invalidLoggins")
  public Object[][] invalidLogins() {
    return new Object[][] {
            new Object[] {"user1@test.com", "wrong_password1", new Response(401, "Invalid email or password")},
            new Object[] {"", "password1", new Response(400, "Email should not be empty string")},
            new Object[] {"user1", "password1", new Response(400, "Invalid email")}
    };
  }

  @Test(
          groups = "negative",
          dataProvider = "invalidLoggins"
  )
  public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse = authenticationService
        .authenticate(email, password);
    SoftAssert sf = new SoftAssert();
    sf.assertEquals(actualResponse.getCode(), expectedResponse.getCode(), expectedResponse.getMessage());
    sf.assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),
        "Response message should be \"Invalid email or password\"");
    sf.assertAll();
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}