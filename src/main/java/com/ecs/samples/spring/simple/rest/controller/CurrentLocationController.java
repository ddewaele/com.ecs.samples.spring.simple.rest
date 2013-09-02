package com.ecs.samples.spring.simple.rest.controller;

import javax.persistence.EntityManager;
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

	final Logger logger = LoggerFactory.getLogger(CurrentLocationController.class);
	
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = "application/json",produces= "application/json")
	public @ResponseBody Location updateCurrentLocation(@RequestBody Location location) {
		long currentTimeMillis = System.currentTimeMillis();
		location.setTimestampMs(currentTimeMillis);
		logger.info("Saving location " + location);
		em.persist(location);
		em.flush();
		return location;
	}
	
	@RequestMapping(method=RequestMethod.GET,produces= "application/json")
	public @ResponseBody Location getCurrentLocation() {
		logger.info("Finding current location");
		String sql = "SELECT l FROM Location l order by l.timestampMs DESC";
		Location location = (Location) em.createQuery(sql).setMaxResults(1).getSingleResult();
		logger.info("Found location = " + location);
		return location;
	}

}