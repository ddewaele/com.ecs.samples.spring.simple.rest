package com.ecs.samples.spring.simple.rest.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_refresh_token")
public class OAuthRefreshToken {

	@Id
	private String token_id;

	@Lob
	private byte[] token;

	@Lob
	private byte[] authentication;

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
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
		return "OAuthRefreshToken [token_id=" + token_id + "]";
	}

}
