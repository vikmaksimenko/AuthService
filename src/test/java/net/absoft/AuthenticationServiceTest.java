package net.absoft;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationServiceTest {

    @Test(
            groups = "positive"
    )
    public void testSuccessfulAuthentication() {
        Response response = new AuthenticationService().authenticate("user1@test.com", "password1");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getCode(), 200, "Response code should be 200");
        softAssert.assertTrue(validateToken(response.getMessage()),
                "Token should be the 32 digits string. Got: " + response.getMessage());
        softAssert.assertAll();
    }

    @Test(
            groups = "negative"
    )
    public void testAuthenticationWithWrongPassword() {
        int expectedCode = 401;
        String expectedMessage = "Invalid email or password";
        Response response = new AuthenticationService()
                .authenticate("user1@test.com", "wrong_password1");

        validateErrorResponse(response, expectedCode, expectedMessage);
    }

    @Test(
            groups = "negative"
    )
    public void testAuthenticationWithEmptyEmail() {
        int expectedCode = 400;
        String expectedMessage = "Email should not be empty string";
        Response response = new AuthenticationService().authenticate("", "password1");

        validateErrorResponse(response, expectedCode, expectedMessage);
    }

    @Test(
            groups = "negative"
    )
    public void testAuthenticationWithInvalidEmail() {
        int expectedCode = 400;
        String expectedMessage = "Invalid email";
        Response response = new AuthenticationService().authenticate("user1", "password1");

        validateErrorResponse(response, expectedCode, expectedMessage);
    }

    @Test(
            groups = "negative"
    )
    public void testAuthenticationWithEmptyPassword() {
        int expectedCode = 400;
        String expectedMessage = "Password should not be empty string";
        Response response = new AuthenticationService().authenticate("user1@test", "");

        validateErrorResponse(response, expectedCode, expectedMessage);
    }

    private boolean validateToken(String token) {
        final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }

    private void validateErrorResponse(Response response, int code, String message) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getCode(), code, "Response code should " + code);
        softAssert.assertEquals(response.getMessage(), message,
                "Response message should be " + "\"" + message + "\"");
        softAssert.assertAll();
    }
}
