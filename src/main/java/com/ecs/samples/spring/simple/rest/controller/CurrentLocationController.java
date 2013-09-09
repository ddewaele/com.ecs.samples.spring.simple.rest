package com.ecs.samples.spring.simple.rest.controller;

import java.security.Principal;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecs.samples.spring.simple.rest.model.Location;
import com.ecs.samples.spring.simple.rest.repository.OAuthAccessTokenRepository;
import com.ecs.samples.spring.simple.rest.repository.OAuthRefreshTokenRepository;
import com.ecs.samples.spring.simple.rest.repository.UserRepository;

@Controller
@RequestMapping("/currentLocation")
public class CurrentLocationController {

	private static final String APPLICATION_JSON = "application/json";

	final Logger logger = LoggerFactory.getLogger(CurrentLocationController.class);
	
	private EntityManager em;
	
	@Resource
	UserRepository simpleUserRepository;
	
	@Resource
	OAuthAccessTokenRepository oAuthAccessTokenRepository;
	
	@Resource
	OAuthRefreshTokenRepository oAuthRefreshTokenRepository;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = APPLICATION_JSON,produces= APPLICATION_JSON)
	public @ResponseBody Location updateCurrentLocation(@RequestBody Location location,Principal principal) {
		long currentTimeMillis = System.currentTimeMillis();
		location.setTimestampMs(currentTimeMillis);
		location.setUserName(principal.getName());
		logger.info("Saving location " + location);
		em.persist(location);
		em.flush();
		return location;
	}
	
	@RequestMapping(method=RequestMethod.GET,produces= APPLICATION_JSON)
	public @ResponseBody Location getCurrentLocation(Principal principal) {
		Location location = null;
		try {
			location = getLocationByPrincipal(principal);
			logger.info("Found current location = " + location);	
		} catch (NoResultException ex) {
			location = new Location();
			logger.info("No current location found. Returning new = " + location);
		}
		
		return location;
	}
	
	
	private Location getLocationByPrincipal(Principal principal) {
		Location locationFromDb;
		TypedQuery<Location> query = em.createNamedQuery("Location.findByUser", Location.class).setParameter("userName", principal.getName()).setMaxResults(1);
		
		try {
			locationFromDb = query.getSingleResult();
		} catch (NoResultException ex) {
			locationFromDb = null;
		}
		return locationFromDb;
	}	

}