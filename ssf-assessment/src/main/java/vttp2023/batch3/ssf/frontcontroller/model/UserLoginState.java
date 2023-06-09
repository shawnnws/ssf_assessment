package vttp2023.batch3.ssf.frontcontroller.model;

public class UserLoginState {

    public static enum AuthStatus  {
        ACCEPTED,
        BAD_REQUEST,
        UNAUTHORIZED,
        INCORRECT_CAPTCHA
    }

    private AuthStatus prevAuthStatus;
    private String message;
    private int invalidLoginAttempt;

    public AuthStatus getPrevAuthEnum() {
        return this.prevAuthStatus;
    }

    public void setPrevAuthEnum(AuthStatus prevAuthStatus) {
        this.prevAuthStatus = prevAuthStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIncorrectLoginAttempt() {
        return this.invalidLoginAttempt;
    }

    public void setIncorrectLoginAttempt(int incorrectLoginAttempt) {
        this.invalidLoginAttempt = incorrectLoginAttempt;
    }

    public UserLoginState(AuthStatus prevAuthStatus, String message, int invalidLoginAttempt) {
        this.prevAuthStatus = prevAuthStatus;
        this.message = message;
        this.invalidLoginAttempt = invalidLoginAttempt;
    }

    public void incrementIncorrectLoginAttempt() {
        this.invalidLoginAttempt += 1;
    }
}

