package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import com.example.demo.dao.UserRepository;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/home")
	public String home(Model m) {

		m.addAttribute("title", "Home - Contact Smart Manager");
		return "home";
	}

	@GetMapping("/")
	public String home1(Model m) {

		m.addAttribute("title", "Home - Contact Smart Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {

		m.addAttribute("title", "About - Contact Smart Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signUp(Model m) {

		m.addAttribute("title", "Register - Smart Contact Manger");
		m.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String doRegister(@ModelAttribute @Valid User user, BindingResult result1,
			@RequestParam(value = "aggrement", defaultValue = "false") boolean aggrement, Model m,
			HttpSession session) {

		try {
			if (!aggrement) {
				System.out.println("You are not followed the terms and conditions");
				throw new Exception("You are not agreed our Terms and Conditions");
			}

			System.out.println("result1" + result1.hasErrors());

			if (result1.hasErrors()) {

				System.out.println("Error" + result1.getAllErrors());
				m.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("aggrement: " + aggrement);
			System.out.println("user: " + user);

			User result = userRepository.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("message", new Message("successfully registered.", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-error"));
			return "signup";
		}
	}

	@GetMapping("/signin")
	public String custumLogin(Model m) {

		m.addAttribute("title", "Login Page");
		return "login";
	}

	@GetMapping("/login")
	public String custumLoginn(Model m) {
        m.addAttribute("title", "Login Page");
		return "login";
	}


	// @PostMapping("/login")
	// public String login(@RequestParam(name = "inputEmail") String inputemail, @RequestParam(name = "inputPassword") String password){

	// 	System.out.println("inputemail: "+inputemail);
	// 	System.out.println("Input password: "+ password);
    //     return "normal/user_dashboard";
	// }
}
