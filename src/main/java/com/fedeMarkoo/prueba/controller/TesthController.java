package com.fedeMarkoo.prueba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TesthController {

	@GetMapping("/testj")
	public String testj() {
		return "test";
	}
}
