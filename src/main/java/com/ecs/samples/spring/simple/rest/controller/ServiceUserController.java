package com.ecs.samples.spring.simple.rest.controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/whoami")
@Controller
public class ServiceUserController {

	@Resource(name="jdbcUserDetailsService")
	private UserDetailsManager userDetailsService;

	
	@ResponseBody
	@RequestMapping("")
	public UserDetails getPhotoServiceUser(Principal principal)
	{
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		return userDetails;
	}
}
