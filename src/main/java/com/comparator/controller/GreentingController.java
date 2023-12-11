package com.comparator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("greeting")
public class GreentingController {
	@GetMapping(value = "{name}")

	public String runCompare(@PathVariable("name") String myName) {
		return String.format("Hello %s", myName);
	}

}
