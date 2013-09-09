package com.ecs.samples.spring.simple.rest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.google.api.client.util.Key;

@Entity
@NamedQueries(value= { 	@NamedQuery(name = "Location.findByUser", query = "from Location l where l.userName= :userName order by l.timestampMs DESC"),
                      	@NamedQuery(name = "Location.findByUserAndTimeStamp", query = "from Location l where l.userName= :userName and l.timestampMs= :timestampMs")})
public class Location {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Key
	private Integer accuracy;
	
	@Key
	private Integer altitude;
	
	@Key
	private Integer altitudeAccuracy;
	
	@Key
	private Integer heading;
	
	@Key
	private Double latitude;
	
	@Key
	private Double longitude;
	
	@Key
	private Double speed;
	
	@Key
	private Long timestampMs;
	
	private String userName;
	
	public Location() {
	}
	
	public Location(Double latitude,Double longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
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
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}	

	@Override
	public String toString() {
		return "Location [accuracy=" + accuracy + ", altitude=" + altitude
				+ ", altitudeAccuracy=" + altitudeAccuracy + ", heading="
				+ heading + ", latitude=" + latitude + ", longitude="
				+ longitude + ", speed=" + speed + ", timestampMs="
				+ timestampMs + "]";
	}
	
	
}
