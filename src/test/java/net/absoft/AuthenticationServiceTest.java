package net.absoft;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeClass(
            groups = {"positive","negative"}
    )
    public void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test(
            groups = "positive"
    )
    public void testSuccessfulAuthentication() {
        Response response = authenticationService.authenticate("user1@test.com", "password1");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getCode(), 200, "Response code should be 200");
        softAssert.assertTrue(validateToken(response.getMessage()),
                "Token should be the 32 digits string. Got: " + response.getMessage());
        softAssert.assertAll();
    }

    @DataProvider(name = "invalidAuthData")
    public Object[][] invalidAuthenticationData() {
        return new Object[][]{
                new Object[]{"user1@test.com", "wrong_password1",
                        new Response(401, "Invalid email or password")},
                new Object[]{"", "password1",
                        new Response(400, "Email should not be empty string")},
                new Object[]{"user1", "password1",
                        new Response(400, "Invalid email")},
                new Object[]{"user1@test.com", "",
                        new Response(400, "Password should not be empty string")},
        };
    }

    @Test(
            groups = "negative",
            dataProvider = "invalidAuthData"
    )
    public void testInvalidAthentication(String email, String password, Response expectedResponse) {
        int expectedCode = expectedResponse.getCode();
        String expectedMessage = expectedResponse.getMessage();
        Response actualResponse = authenticationService.authenticate(email, password);

        validateErrorResponse(actualResponse, expectedCode, expectedMessage);
    }

//    @Test(
//            groups = "negative"
//    )
//    public void testAuthenticationWithWrongPassword() {
//        int expectedCode = 401;
//        String expectedMessage = "Invalid email or password";
//        Response response = authenticationService.authenticate("user1@test.com", "wrong_password1");
//
//        validateErrorResponse(response, expectedCode, expectedMessage);
//    }
//
//    @Test(
//            groups = "negative"
//    )
//    public void testAuthenticationWithEmptyEmail() {
//        int expectedCode = 400;
//        String expectedMessage = "Email should not be empty string";
//        Response response = authenticationService.authenticate("", "password1");
//
//        validateErrorResponse(response, expectedCode, expectedMessage);
//    }
//
//    @Test(
//            groups = "negative"
//    )
//    public void testAuthenticationWithInvalidEmail() {
//        int expectedCode = 400;
//        String expectedMessage = "Invalid email";
//        Response response = authenticationService.authenticate("user1", "password1");
//
//        validateErrorResponse(response, expectedCode, expectedMessage);
//    }
//
//    @Test(
//            groups = "negative"
//    )
//    public void testAuthenticationWithEmptyPassword() {
//        int expectedCode = 400;
//        String expectedMessage = "Password should not be empty string";
//        Response response = authenticationService.authenticate("user1@test", "");
//
//        validateErrorResponse(response, expectedCode, expectedMessage);
//    }

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
