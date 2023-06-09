package vttp2023.batch3.ssf.frontcontroller.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class User {
    
    @Min(value = 2, message = "Username must be more than 2 characters")
    @NotNull(message = "Username cannot be empty")
    private String username;

    @Min(value = 2, message = "Password must be more than 2 characters")
    @NotNull(message = "Password cannot be empty")
    private String password;

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

    
}
