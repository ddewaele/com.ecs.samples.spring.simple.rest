package com.ecs.samples.spring.simple.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimpleRestController {

	@RequestMapping(value="helloWorld", method=RequestMethod.GET)
	@ResponseBody
	public String helloWorld() {
		return "Hello World !";
	}
}
