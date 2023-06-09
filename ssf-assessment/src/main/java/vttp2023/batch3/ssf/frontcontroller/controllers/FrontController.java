package vttp2023.batch3.ssf.frontcontroller.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp2023.batch3.ssf.frontcontroller.model.Captcha;
import vttp2023.batch3.ssf.frontcontroller.model.LoginUser;
import vttp2023.batch3.ssf.frontcontroller.model.UserLoginState;
import vttp2023.batch3.ssf.frontcontroller.services.AuthenticationService;

@Controller
public class FrontController {

	@Autowired
	AuthenticationService authenticationService;

	// TODO: Task 2, Task 3, Task 4, Task 6
	@GetMapping(path="/")
	public String showLoginPage(Model model) {
		/**
		 * Renders the login page (view0) to the user
		 */
		LoginUser newUser = new LoginUser();
        model.addAttribute("user", newUser);
		return "view0";
	}

	@PostMapping(path="/login")
	public String loginUser(Model model, HttpSession sess, @Valid LoginUser user, BindingResult bindings) {
		String username = user.getUsername();
		String password = user.getPassword();
		
		if (!LoginUser.validUsername(username) || !LoginUser.validPassword(password)) {
			if (!LoginUser.validUsername(username)) {
				model.addAttribute("usernameError", "Username should be at least 2 characters");
			}
			if (!LoginUser.validPassword(password)) {
				model.addAttribute("passwordError", "Password should be at least 2 characters");
			}
			return "view0";
		}	

		Captcha previousCaptcha = Captcha.retrieveCaptcha(sess, username);
		
		if (previousCaptcha != null) {
			System.out.println("Captcha Question: " + previousCaptcha.getQuestion());
			System.out.println("Captcha Answer: " + previousCaptcha.getAnswer());
			System.out.println("User Answer: " + user.getCaptchaAnswer());
		}
        boolean captchaIsCorrect = Captcha.captchaCorrectAnswer(previousCaptcha, user.getCaptchaAnswer());

		if (!captchaIsCorrect) {
            UserLoginState newAuthState = authenticationService.updateUserAsIncorrectCaptcha(username);
            String remainingAttemptErrorMessage = processRemainingLoginAttempts(newAuthState, username, model, sess);
            refreshUser(model);
            model.addAttribute("error", "incorrect answer to captcha" + remainingAttemptErrorMessage);
            return "view0";
        }

		try {
			authenticationService.authenticate(username, password);

			UserLoginState authState = authenticationService.getAuthState(username);

			if (authState.getPrevAuthEnum() == UserLoginState.AuthStatus.ACCEPTED) {
				setUsernameInSession(sess, username);
				authenticationService.markUserAsLoggedIn(username);
				return "view1";
			} 
			else {
				UserLoginState newAuthState = authenticationService.updateUserAsUnauthorized(username);
				String remainingAttemptErrorMessage = processRemainingLoginAttempts(newAuthState, username, model, sess);
				String unauthorizedErrorMessage = authState.getMessage();
				model.addAttribute("error", unauthorizedErrorMessage + ". " + remainingAttemptErrorMessage);
				return "view0";
			}
		} catch (Exception e) {
			model.addAttribute("error",  e.getMessage());
			return "view0";
		}
	}
	
	public String processRemainingLoginAttempts(UserLoginState newAuthState, String username, Model model, HttpSession sess) {
		/**
		 * Checks if a given user still have login attempts
		 * 
		 * If the user has no more login attempts...
		 * Reset the login attempt to 3, disable the user
		 */
        int remainingLoginAttempts = 3 - newAuthState.getIncorrectLoginAttempt();
        String errorMessage;

        if (remainingLoginAttempts == 0) {
            authenticationService.resetIncorrectLoginAttempts(username);
            authenticationService.disableUser(username);
            errorMessage = String.format("Reached 3 maximum login attempts for user %s, login for user disabled for 30 minutes", username);
        } 
		else {
            errorMessage = String.format("User %s has %d remaining login attempts", username, remainingLoginAttempts);
            refreshCaptcha(model, sess, username);
        }
        return errorMessage;
    }

    public void refreshUser(Model model) {
		/**
		 * Refreshes the 
		 */
        LoginUser newUser = new LoginUser();
        model.addAttribute("user", newUser);
    }
	
	public void refreshCaptcha(Model model, HttpSession sess, String username) {
		/**
		 * Regenerate the captcha for a given username, and sets it in the HttpSession
		 */
        Captcha captcha = Captcha.regenerateCaptcha(true);
        model.addAttribute("captcha", captcha);
        Captcha.setCaptcha(sess, captcha, username);
    }

	public String getUsernameInSession(HttpSession session) {
        return (String) session.getAttribute("currentUsername");
    }

    public void setUsernameInSession(HttpSession session, String username) {
        session.setAttribute("currentUsername", username);
    }
}
