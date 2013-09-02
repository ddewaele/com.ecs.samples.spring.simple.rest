package com.ecs.samples.spring.simple.rest.controller;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecs.samples.spring.simple.rest.model.Location;

@Controller
@RequestMapping("/currentLocation")
public class CurrentLocationController {

	private static final String APPLICATION_JSON = "application/json";

	private static final String SQL_CURRENT_LOCATION = "SELECT l FROM Location l order by l.timestampMs DESC";

	final Logger logger = LoggerFactory.getLogger(CurrentLocationController.class);
	
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = APPLICATION_JSON,produces= APPLICATION_JSON)
	public @ResponseBody Location updateCurrentLocation(@RequestBody Location location) {
		long currentTimeMillis = System.currentTimeMillis();
		location.setTimestampMs(currentTimeMillis);
		logger.info("Saving location " + location);
		em.persist(location);
		em.flush();
		return location;
	}
	
	@RequestMapping(method=RequestMethod.GET,produces= APPLICATION_JSON)
	public @ResponseBody Location getCurrentLocation() {
		logger.info("Finding current location");
		String sql = SQL_CURRENT_LOCATION;
		Location location = null;
		try {
			location = (Location) em.createQuery(sql).setMaxResults(1).getSingleResult();
			logger.info("Found current location = " + location);	
		} catch (NoResultException ex) {
			location = new Location();
			logger.info("No current location found. Returning new = " + location);
		}
		
		return location;
	}

}