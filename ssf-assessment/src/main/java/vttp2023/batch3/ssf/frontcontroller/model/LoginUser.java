package vttp2023.batch3.ssf.frontcontroller.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class LoginUser {
    
    @Min(value = 3, message = "Username must be more than 2 characters")
    @NotNull(message = "Username cannot be empty")
    private String username;

    @Min(value = 3, message = "Password must be more than 2 characters")
    @NotNull(message = "Password cannot be empty")
    private String password;

    @NotEmpty
    private String captchaAnswer;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptcha(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

    public static boolean validUsername(String userName) {
		return userName.length() >= 2;
	}

	public static boolean validPassword(String password) {
		return password.length() >= 2;
	}
}
