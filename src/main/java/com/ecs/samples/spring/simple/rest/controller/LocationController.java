package com.ecs.samples.spring.simple.rest.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecs.samples.spring.simple.rest.model.Location;

@Controller
@RequestMapping("/location")
public class LocationController {

	final Logger logger = LoggerFactory.getLogger(LocationController.class);
	
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = "application/json",produces= "application/json")
	public @ResponseBody Location addLocation(@RequestBody Location location) {
		logger.info("Saving location " + location);
		em.persist(location);
		em.flush();
		return location;
	}
	
	@RequestMapping(value="{timestamp}",method=RequestMethod.GET,produces= "application/json")
	public @ResponseBody Location getLocation(@PathVariable Long timestamp) {
		logger.info("Findign location with Timestamp = " + timestamp);
		Location location = em.find(Location.class, timestamp);
		logger.info("Found location = " + location);
		return location;
	}
	
	@RequestMapping(method=RequestMethod.GET,produces= "application/json")
	public @ResponseBody List<Location> getLocations(@RequestParam(value="min-time",required=false) Long minTime,@RequestParam(value="max-time",required=false) Long maxTime) {
		logger.info("Finding locations with min-time = {} and max-time = {}",minTime,maxTime);
		String sql = "SELECT l FROM Location l";
		
		if ((minTime!=null) && (maxTime!=null)) {
			sql+= " where l.timestampMs >= " + minTime + " and l.timestampMs <= " + maxTime;
		} else {
			if (minTime!=null) {
				sql+= " where l.timestampMs >= " + minTime;				
			} else if (maxTime!=null) {
				sql+= " where l.timestampMs <= " + maxTime;
			}
		}
		Query query = em.createQuery(sql);
		List<Location> resultList = query.setMaxResults(1000).getResultList();
		return resultList;
	}
	
}