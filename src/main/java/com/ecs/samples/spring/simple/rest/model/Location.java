package com.ecs.samples.spring.simple.rest.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Location {
	
	private Integer accuracy;
	private Integer altitude;
	private Integer altitudeAccuracy;
	private Integer heading;
	private Double latitude;
	private Double longitude;
	private Double speed;
	
	@Id
	private Long timestampMs;
	
	public Location() {
	}
	
	public Location(Double latitude,Double longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public Integer getAltitude() {
		return altitude;
	}

	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}

	public Integer getAltitudeAccuracy() {
		return altitudeAccuracy;
	}

	public void setAltitudeAccuracy(Integer altitudeAccuracy) {
		this.altitudeAccuracy = altitudeAccuracy;
	}

	public Integer getHeading() {
		return heading;
	}

	public void setHeading(Integer heading) {
		this.heading = heading;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Long getTimestampMs() {
		return timestampMs;
	}

	public void setTimestampMs(Long timestampMs) {
		this.timestampMs = timestampMs;
	}
	
	
}
