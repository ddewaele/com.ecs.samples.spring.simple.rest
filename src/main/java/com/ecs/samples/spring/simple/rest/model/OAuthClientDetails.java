package com.ecs.samples.spring.simple.rest.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="OAUTH_CLIENT_DETAILS")
public class OAuthClientDetails {

	@Id
	private String client_id;
	private String client_secret;
	private String resource_ids;
	private String scope;
	
	private String authorized_grant_types;
	private String web_server_redirect_uri;
	private String authorities;
	private String access_token_validity;
	private String refresh_token_validity;
	private String additional_information;
	
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getResource_ids() {
		return resource_ids;
	}
	public void setResource_ids(String resource_ids) {
		this.resource_ids = resource_ids;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getAuthorized_grant_types() {
		return authorized_grant_types;
	}
	public void setAuthorized_grant_types(String authorized_grant_types) {
		this.authorized_grant_types = authorized_grant_types;
	}
	public String getWeb_server_redirect_uri() {
		return web_server_redirect_uri;
	}
	public void setWeb_server_redirect_uri(String web_server_redirect_uri) {
		this.web_server_redirect_uri = web_server_redirect_uri;
	}
	public String getAuthorities() {
		return authorities;
	}
	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}
	public String getAccess_token_validity() {
		return access_token_validity;
	}
	public void setAccess_token_validity(String access_token_validity) {
		this.access_token_validity = access_token_validity;
	}
	public String getRefresh_token_validity() {
		return refresh_token_validity;
	}
	public void setRefresh_token_validity(String refresh_token_validity) {
		this.refresh_token_validity = refresh_token_validity;
	}
	public String getAdditional_information() {
		return additional_information;
	}
	public void setAdditional_information(String additional_information) {
		this.additional_information = additional_information;
	}

	
}
