package com.ecs.samples.spring.simple.rest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_refresh_token")
public class OAuthRefreshToken {

	@Id
	@Column(name="token_id")
	private String id;

	@Lob
	private byte[] token;

	@Lob
	private byte[] authentication;

	public String getId() {
		return id;
	}

	public void setId(String token_id) {
		this.id = token_id;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	@Override
	public String toString() {
		return "OAuthRefreshToken [token_id=" + id + "]";
	}

}
