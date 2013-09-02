package com.ecs.samples.spring.simple.rest.controller;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;

public class OAuth2Test {

  private static final String API_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/location";

private static final String OAUTH_AUTH_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/authorize";

private static final String OAUTH_TOKEN_URL = "http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/token";

/** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /** OAuth 2.0 scopes. */
  private static final List<String> SCOPES = Arrays.asList(
      "read",
      "write",
      "trust");

  private static Oauth2 oauth2;
  private static GoogleClientSecrets clientSecrets;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(OAuth2Test.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
   
    AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod() , 
        httpTransport, 
        JSON_FACTORY, 
        new GenericUrl(OAUTH_TOKEN_URL), 
        new ClientParametersAuthentication(clientSecrets.getDetails().getClientId(),clientSecrets.getDetails().getClientSecret()), 
        clientSecrets.getDetails().getClientId(), 
        OAUTH_AUTH_URL).setScopes(SCOPES).setDataStoreFactory(
            dataStoreFactory).build();

    
    
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

      Credential credential = authorize();
      oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).build();
      
      HttpRequest request = httpTransport.createRequestFactory(credential).buildGetRequest(new GenericUrl(API_URL));
      String parseAsString = request.execute().parseAsString();
      System.out.println(parseAsString);
      
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
