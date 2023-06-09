package vttp2023.batch3.ssf.frontcontroller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vttp2023.batch3.ssf.frontcontroller.model.User;

@Controller
public class FrontController {

	// TODO: Task 2, Task 3, Task 4, Task 6
	@GetMapping(path="/")
	public String loginPage(Model model) {

		model.addAttribute("user", new User());
		return "view0";
	}
}
