package vttp2023.batch3.ssf.frontcontroller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vttp2023.batch3.ssf.frontcontroller.services.AuthenticationService;

@RestController
public class ProtectedController {

	@Autowired
    private AuthenticationService authenticationService;

	// TODO Task 5
	// Write a controller to protect resources rooted under /protected

	@GetMapping(path="/protected/{viewName}")
    public String getProtected(@PathVariable String viewName, Model model, HttpSession sess) {
        String authStateUsername = getCurrentUserName(sess);
        boolean isAuthenticated = authenticationService.userIsLoggedIn(authStateUsername);
        if (!isAuthenticated) {
            return "redirect:/";
        } else {
            return viewName;
        }
    }

    public String getCurrentUserName(HttpSession session) {
        /**
         * Gets the username from the HttpSession
         */
        return (String) session.getAttribute("currentUsername");
    }
}

