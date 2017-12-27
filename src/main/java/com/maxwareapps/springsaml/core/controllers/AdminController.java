package com.maxwareapps.springsaml.core.controllers;

import com.maxwareapps.springsaml.core.stereotypes.CurrentUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

	@RequestMapping("/admin")
	public String adminPanel(@CurrentUser User user, Model model) {
		model.addAttribute("username", 	user.getUsername());
		return "admin";
	}

}
