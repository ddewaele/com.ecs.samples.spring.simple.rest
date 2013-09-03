package com.ecs.samples.spring.simple.rest.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecs.samples.spring.simple.rest.model.OAuthAccessToken;
import com.ecs.samples.spring.simple.rest.model.OAuthRefreshToken;
import com.ecs.samples.spring.simple.rest.model.User;
import com.ecs.samples.spring.simple.rest.repository.OAuthAccessTokenRepository;
import com.ecs.samples.spring.simple.rest.repository.OAuthRefreshTokenRepository;
import com.ecs.samples.spring.simple.rest.repository.UserRepository;

@Controller
@RequestMapping("/test")
public class TestController {

	private static final String APPLICATION_JSON = "application/json";

	final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Resource
	UserRepository simpleUserRepository;
	
	@Resource
	OAuthAccessTokenRepository oAuthAccessTokenRepository;
	
	@Resource
	OAuthRefreshTokenRepository oAuthRefreshTokenRepository;
	
	
	@RequestMapping(method=RequestMethod.GET,produces= APPLICATION_JSON)
	public @ResponseBody String test() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails details = (UserDetails) authentication.getPrincipal();
			String username = details.getUsername();
			logger.info(" +++++++++++++ username = " + username);
		}

		
		logger.info("Found simpleUserRepository " + simpleUserRepository);

		logger.info(" ------------------------------------------- ");
		logger.info("looping users");
		logger.info(" ------------------------------------------- ");
		Iterable<User> users = simpleUserRepository.findAll();
		for (User user : users) {
			logger.info("Found user " + user);
		}
		
		logger.info(" ------------------------------------------- ");
		logger.info("looping refresh tokens");
		logger.info(" ------------------------------------------- ");
		Iterable<OAuthRefreshToken> refreshTokens = oAuthRefreshTokenRepository.findAll();
		for (OAuthRefreshToken oAuthRefreshToken : refreshTokens) {
			logger.info("Found refresh token " + oAuthRefreshToken);
		}
		
		logger.info(" ------------------------------------------- ");
		logger.info("looping refresh tokens");
		logger.info(" ------------------------------------------- ");
		Iterable<OAuthAccessToken> accessTokens = oAuthAccessTokenRepository.findAll();
		for (OAuthAccessToken oAuthAccessToken : accessTokens) {
			logger.info("Found access token " + oAuthAccessToken);
		}

		return "OK";
	}

}