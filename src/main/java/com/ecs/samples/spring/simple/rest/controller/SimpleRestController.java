package com.ecs.samples.spring.simple.rest.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecs.samples.spring.simple.rest.model.Location;

@Controller
@RequestMapping("/location")
public class SimpleRestController {

	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = "application/json",produces= "application/json")
	public @ResponseBody Location addLocation(@RequestBody Location location) {
		long currentTimeMillis = System.currentTimeMillis();
		location.setTimestampMs(currentTimeMillis);
		System.out.println("Saving location " + location);
		em.persist(location);
		em.flush();
		return location;
	}
	
	@RequestMapping(value="{timestamp}",method=RequestMethod.GET,produces= "application/json")
	public @ResponseBody Location getLocation(@PathVariable Long timestamp) {
		System.out.println("Findign location with Timestamp = " + timestamp);
		Location location = em.find(Location.class, timestamp);
		System.out.println("Found location = " + location);
		return location;
	}
	
}