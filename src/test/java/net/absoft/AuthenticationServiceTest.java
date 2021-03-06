package net.absoft;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.Test;

public class AuthenticationServiceTest {

  @Test
  public void testSuccessfulAuthentication() {
    Response response = new AuthenticationService().authenticate("user1@test.com", "password1");
    assertEquals(response.getCode(), 200, "Response code should be 200");
    assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
  }

  @Test
  public void testAuthenticationWithWrongPassword() {
    Response response = new AuthenticationService()
        .authenticate("user1@test.com", "wrong_password1");
    assertEquals(response.getCode(), 401, "Response code should be 401");
    assertEquals(response.getMessage(), "Invalid email or password",
        "Response message should be \"Invalid email or password\"");
  }

  @Test
  public void testAuthenticationWithEmptyEmail() {
    Response response = new AuthenticationService().authenticate("", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Email should not be empty string",
        "Response message should be \"Email should not be empty string\"");
  }

  @Test
  public void testAuthenticationWithInvalidEmail() {
    Response response = new AuthenticationService().authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 200");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
  }

  @Test
  public void testAuthenticationWithEmptyPassword() {
    Response response = new AuthenticationService().authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
