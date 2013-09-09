package com.ecs.samples.spring.simple.rest.controller;

import java.security.Principal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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

	private static final int MAX_RESULTS = 1000;

	private static final String QUERY_MAX_TIME = "max-time";

	private static final String QUERY_MIN_TIME = "min-time";

	private static final String APPLICATION_JSON = "application/json";

	final Logger logger = LoggerFactory.getLogger(LocationController.class);
	
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	
	/**
	 * 
	 * Adds or updates a location to the location history of the user.
	 * The location needs to contain a timeStamp.
	 * If a location was found in the DB the attributes are updated.
	 * If no location was found a new one is created.
	 * 
	 * @param location the location that needs to be added to the history.
	 * @param principal the authenticated user, as injected by Spring 
	 * 
	 * @return The newly added location.	
	 * 
	 */
	@Transactional
	@RequestMapping(method=RequestMethod.POST,consumes = APPLICATION_JSON,produces= APPLICATION_JSON)
	public @ResponseBody Location addLocation(@RequestBody Location location,Principal principal) {
		if (location==null) {
			throw new IllegalArgumentException("A valid location is required");
		}
		if (principal==null) {
			throw new IllegalArgumentException("A valid principal is required");
		}
		
		Location locationFromDb = null;
		Long timestampMs = location.getTimestampMs();
		
		if (timestampMs!=null) {
			locationFromDb = getLocationByTimestampAndPrincipal(timestampMs,principal);
		} else {
			throw new RuntimeException("Location needs to contain a timeStamp");
		}
		
		if (locationFromDb!=null) {
			logger.info("Updating location " + location);
			locationFromDb.setAccuracy(location.getAccuracy());
			locationFromDb.setAltitude((location.getAltitude()));
			locationFromDb.setAltitudeAccuracy(location.getAltitudeAccuracy());
			locationFromDb.setHeading(location.getHeading());
			locationFromDb.setLatitude(location.getLatitude());
			locationFromDb.setLongitude(location.getLongitude());
			locationFromDb.setSpeed(location.getSpeed());
		} else {
			logger.info("Saving location " + location);
			location.setUserName(principal.getName());
			em.persist(location);
		}
		
		return location;
	}


	private Location getLocationByTimestampAndPrincipal(Long timestampMs,Principal principal) {
		Location locationFromDb;
		TypedQuery<Location> query = em.createNamedQuery("Location.findByUserAndTimeStamp", Location.class).setParameter("userName", principal.getName()).setParameter("timestampMs", timestampMs);
		
		try {
			locationFromDb = query.getSingleResult();
		} catch (NoResultException ex) {
			locationFromDb = null;
		}
		return locationFromDb;
	}
	
	/**
	 * 
	 * Gets the location for a specific timestamp
	 * 
	 * @param timestamp
	 * @return
	 */
	@RequestMapping(value="{timestamp}",method=RequestMethod.GET,produces= APPLICATION_JSON)
	public @ResponseBody Location getLocation(@PathVariable Long timestamp,Principal principal) {
		logger.info("Findign location with Timestamp = " + timestamp);
		Location location = getLocationByTimestampAndPrincipal(timestamp,principal);
		logger.info("Found location = " + location);
		return location;
	}
	
	
	/**
	 * 
	 * Gets a list of locations
	 * 
	 * @param minTime
	 * @param maxTime
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET,produces= APPLICATION_JSON)
	public @ResponseBody List<Location> getLocations(@RequestParam(value=QUERY_MIN_TIME,required=false) Long minTime,@RequestParam(value=QUERY_MAX_TIME,required=false) Long maxTime) {
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
		List<Location> resultList = query.setMaxResults(MAX_RESULTS).getResultList();
		return resultList;
	}
	
}