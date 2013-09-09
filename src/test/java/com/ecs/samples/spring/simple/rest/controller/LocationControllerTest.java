package com.ecs.samples.spring.simple.rest.controller;

import java.security.Principal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ecs.samples.spring.simple.rest.model.Location;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-servlet.xml" })
public class LocationControllerTest {

	@Autowired
	private LocationController locationController;

	@Autowired
	private CurrentLocationController currentLocationController;

	private Principal user2;
	private Principal user1;
	private Principal user3;
	
	@Before
	public void setup() {
		this.user1 = new MyPrincipal("user1");
		this.user2 = new MyPrincipal("user2");
		this.user3 = new MyPrincipal("user3");
	}
	
	@Test
	public void addPastLocations() {
		Location location1 = createLocationForUser(10.0, 10.0,this.user1);
		Location location2 = createLocationForUser(11.0, 11.0,this.user1);
		Location location3 = createLocationForUser(12.0, 12.0,this.user1);
		Location location4 = createLocationForUser(10.0, 10.0,this.user2);
		
		Assert.assertNotNull(location1.getTimestampMs());
		
		Assert.assertNotNull(locationController.getLocation(location1.getTimestampMs(),this.user1));
		Assert.assertNotNull(locationController.getLocation(location2.getTimestampMs(),this.user1));
		Assert.assertNotNull(locationController.getLocation(location3.getTimestampMs(),this.user1));
		
		Assert.assertNull(locationController.getLocation(location1.getTimestampMs(),this.user2));
		Assert.assertNull(locationController.getLocation(location2.getTimestampMs(),this.user2));
		Assert.assertNull(locationController.getLocation(location3.getTimestampMs(),this.user2));
		
		Assert.assertNotNull(locationController.getLocation(location4.getTimestampMs(),this.user2));
		
		Assert.assertNull(locationController.getLocation(location1.getTimestampMs(),this.user3));
		Assert.assertNull(locationController.getLocation(location2.getTimestampMs(),this.user3));
		Assert.assertNull(locationController.getLocation(location3.getTimestampMs(),this.user3));
	}
	
	@Test
	public void addCurrentLocation() {
		createCurrentLocationForUser(10.0, 20.0,this.user1);
		Location currentLocation = currentLocationController.getCurrentLocation(this.user1);
		Assert.assertEquals(10.0, currentLocation.getLatitude().doubleValue(),0d);
		Assert.assertEquals(20.0, currentLocation.getLongitude().doubleValue(),0d);
		
		createCurrentLocationForUser(30.0, 40.0,this.user1);
		currentLocation = currentLocationController.getCurrentLocation(this.user1);
		Assert.assertEquals(30.0, currentLocation.getLatitude().doubleValue(),0d);
		Assert.assertEquals(40.0, currentLocation.getLongitude().doubleValue(),0d);
		
	}
	
	

	private Location createCurrentLocationForUser(double lat,double lng,Principal principal) {
		Location location = new Location(lat,lng);
		Location returnedLocation = currentLocationController.updateCurrentLocation(location, principal);
		return returnedLocation;
	}

	private Location createLocationForUser(double lat,double lng,Principal principal) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		
		Location location = new Location(lat,lng);
		location.setTimestampMs(cal.getTimeInMillis());
		
		Location returnedLocation = locationController.addLocation(location, principal);
		
		return returnedLocation;
	}
	
	class MyPrincipal implements Principal {
		
		private String name;

		public MyPrincipal(String name) {
			this.name=name;
		}
		
		@Override
		public String getName() {
			return this.name;
		}
	};
}
