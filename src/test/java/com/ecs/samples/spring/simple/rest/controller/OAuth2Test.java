package com.ecs.samples.spring.simple.rest.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.ecs.samples.spring.simple.rest.model.Location;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

public class OAuth2Test {

	private static final String USER_ID = "user";

	private static final String API_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/currentLocation";

	private static final String OAUTH_AUTH_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/authorize";

	private static final String OAUTH_TOKEN_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/token";

	private static final String OAUTH_CLIENT_ID = "my-trusted-client";
	private static final String OAUTH_CLIENT_SECRET = "somesecret";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");

	private static FileDataStoreFactory dataStoreFactory;

	private static HttpTransport httpTransport;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static final List<String> SCOPES = Arrays.asList("location","");


	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {

		AuthorizationCodeFlow flow = getAuthorizationCodeFlow();

		// authorize
		return new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize(USER_ID);
	}

	private static AuthorizationCodeFlow getAuthorizationCodeFlow()
			throws IOException {
		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(), httpTransport,
				JSON_FACTORY, new GenericUrl(OAUTH_TOKEN_URL),
				new ClientParametersAuthentication(OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET), OAUTH_CLIENT_ID, OAUTH_AUTH_URL).setScopes(SCOPES)
				.setDataStoreFactory(dataStoreFactory).build();
		return flow;
	}

	public static void main(String[] args) {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			
			
			DataStore<StoredCredential> defaultDataStore = StoredCredential.getDefaultDataStore(dataStoreFactory);
			StoredCredential storedCredential = defaultDataStore.get(USER_ID);
			
			Credential credential = getAuthorizationCodeFlow().loadCredential(USER_ID);
			
			if (storedCredential==null) {
				credential = authorize();	
			}

			Location location = new Location(10.123, 20.456);
			
			HttpRequest request = null;
			
			request = httpTransport.createRequestFactory(credential).buildPostRequest(new GenericUrl(API_URL),new JsonHttpContent(JSON_FACTORY, location));
			System.out.println(request.execute().parseAsString());			
			request = httpTransport.createRequestFactory(credential).buildGetRequest(new GenericUrl(API_URL));
			System.out.println(request.execute().parseAsString());

			// success!
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}

}
