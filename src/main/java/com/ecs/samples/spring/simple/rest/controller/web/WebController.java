package com.ecs.samples.spring.simple.rest.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {

	   @RequestMapping(value = "/", method = RequestMethod.GET)
	   public String index() {
		   return "index";
	   }
	   
	   @RequestMapping(value = "/index", method = RequestMethod.GET)
	   public String index2() {
		   return "index";
	   }	   
}
