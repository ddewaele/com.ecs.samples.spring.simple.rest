#Introduction

The goal of this project project is to bootstrap a secure Spring MVC REST API.
The API included in this project allows for you to manage your locations.

It exposes an API that allows you to

- Update your current location
- Retrieve your current location
- Retrieve your location history
- Update a past location
- Remove a location (TODO).

See the setup section on how to run the project.

# Setup

This section describes the local setup that is required to run the project. We've tried to kept the dependencies to a minimum.

## Environment variables

*Optional : * You can export the following environment variables to facilitate testing.

### Local setup
	
	export host=localhost
	export port=6002
	export location_endpoint=http://${host}:${port}/com.ecs.samples.spring.simple.rest/location
	export current_location_endpoint=http://${host}:${port}/com.ecs.samples.spring.simple.rest/currentLocation

### System properties

You can export the following maven options so that the project is statted in debug mode when launched through `mvn tomcat:run`.
It allows for a very convenient way to test the application.

- on Linux / Unix / Mac OS X

		export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -DlogFileLocation=/tmp"

- on Windows :

		set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -DlogFileLocation=C:/TEMP

### OAuth test

There is a Oauth2Test class inclued in the project that allows you to launch an oauth2 flow.
It pops the browser to handle the authentication / authorization flow, and stores its tokens in the following location:

	~/.store/oauth2_sample/StoredCredential 

In order to remove that local cache, simply remove the folder before running the test again,.

	rm -rf ~/.store/oauth2_sample/StoredCredential

# Spring

Spring is used heavily in this project. (elaborate) 

## Spring context

## Annotation and component scanning

- Added `<context:annotation-config />` allowing us to activate annotations in beans already registered in the application context (no matter if they were defined with XML or by package scanning).
- Added `<context:component-scan>` to scan packages to find and register beans within the application context.
- Added `<mvc:annotation-driven>` to support for new Spring MVC features such as declarative validation with @Valid, HTTP message conversion with @RequestBody/@ResponseBody, new field conversion architecture, etc
- Added `<tx:annotation-driven>` to enable the transactional behavior based on annotations.

##EntityManagerFactory

As we are in the business of storing / retrieving locations we need a persistence layer in place.
We're using standard JPA inside our Spring container, using Hibernate as the vendor. 

The first thing we need to define is an EntityManagerFactory. The simplest way to create an EntityManagerFactory bean is to define it like this:
	
	<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="persistenceUnitName" value="spring-jpa" />
		<property name="jpaVendorAdapter">
			<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
				<property name="showSql" value="true" />
				<property name="generateDdl" value="true" />
				<property name="database" value="HSQL" />
			</bean>
		</property>
	</bean>
    
Notice how we point to

- a dataSource (this can point to a simple embedded database)
- a persistenceUnitName (points to the pu defined in the persistence.xml)
- a jpaVendorAdapter (uses the hibernate specific entity manager)

## The datasource

For the moment we're using an embedded database (HSQL). At a later stage this will be replaced with an actual database.

	<jdbc:embedded-database id="dataSource" type="HSQL" />    


## The persistence.xml

Notice how the `EntityManagerFactory` points to a `persistenceUnitName`. The `persistenceUnitName` is defined in a `persistence.xml` file that is located in the META-INF folder of the package that holds the Entity classes.

	<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
		<persistence-unit name="spring-jpa">
			<provider>org.hibernate.ejb.HibernatePersistence</provider>
		</persistence-unit>
	</persistence>

## The transactionManager

We also need a transactionManager for our entityManager. 

	<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
			<property name="entityManagerFactory" ref="entityManagerFactory" />
	</bean>
	
# OAuth2

The REST API used in this project is being secured through Oauth2. This means that in order to access the API you need to be authorized to do so.

Attempting to access the API without being authorized results in the following error

	< HTTP/1.1 401 Unauthorized
	< Server: Apache-Coyote/1.1
	< Cache-Control: no-store
	< Pragma: no-cache
	< WWW-Authenticate: Bearer realm="sparklr2", error="unauthorized", error_description="An Authentication object was not found in the SecurityContext"
	< Content-Type: application/json;charset=UTF-8
	< Transfer-Encoding: chunked
	< Date: Tue, 03 Sep 2013 07:43:57 GMT
	< 
	* Connection #0 to host localhost left intact
	* Closing connection #0
	{"error":"unauthorized","error_description":"An Authentication object was not found in the SecurityContext"}

## web.xml

It's important to realize that all configuration done in the Spring Context is only put into effect if we have the following filter in place. 

	<filter>
		<filter-name>springSecurityFilterChain</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<init-param>
			<param-name>contextAttribute</param-name>
			<param-value>org.springframework.web.servlet.FrameworkServlet.CONTEXT.spring</param-value>
		</init-param>
	</filter>

	<filter-mapping>
		<filter-name>springSecurityFilterChain</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

In order to use Oauth2 in Spring MVC, we require a lot of configuration in the context.

## Spring Context setup

### Protected URLS

The first thing we'll do is list our protected URLs.

	<http pattern="/location/**" create-session="never" entry-point-ref="oauthAuthenticationEntryPoint"
		access-decision-manager-ref="accessDecisionManager" xmlns="http://www.springframework.org/schema/security">
		<anonymous enabled="false" />
		<intercept-url pattern="/location" access="ROLE_USER,SCOPE_LOCATIONHISTORY" />
		<custom-filter ref="resourceServerFilter" before="PRE_AUTH_FILTER" />
		<access-denied-handler ref="oauthAccessDeniedHandler" />
	</http>
	
This means that everyone attempting to access `/location/*` on our REST API will need to be authenticated and authorized to do so.
We also only allow entities with the role `USER` andd acope `LOCATIONHISTORY` to access this resource.
	
Notice how this `http` definition relies on

- Authentication Entry Point
- AccessDeniedHandler
- Custom Filter
- Access Decision Manager

We'll cover these different components in details in the following sections. 

### Authentication Entry Point

An Authentication Entry Point capable of commencing an authentication scheme. If authentication fails and the caller has asked for a specific content type response, this entry point can send one, along with a standard 401 status

	<bean id="oauthAuthenticationEntryPoint" class="org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint">
		<property name="realmName" value="sparklr2" />
	</bean>
	
This bean kicks in when things go wrong .... If for example we try to access the API without providing the proper authentication credentials the Spring security framework will throw an `AuthenticationCredentialsNotFoundException`.
(other examples of exceptions include expired accounts, locked accounts, invalid account credentials,....)

When such an authentication error occurs, this `OAuth2AuthenticationEntryPoint` (that also acts as an ExceptionHandler) will handle the exception by enhancing the response.

It will add the `WWW-Authenticate` HTTP header with the following value
 
	Bearer realm="sparklr2", error="unauthorized", error_description="An Authentication object was not found in the SecurityContext" 

### AccessDeniedHandler

If authorization fails and the caller has asked for a specific content type response, this entry point can send one, along with a standard 403 status. 

	<bean id="oauthAccessDeniedHandler" class="org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler" />

### Custom Filter

OAuth Resource Server ( = API server used to access the user's information.) is defined by a `resource id` and a `token service`.

	<oauth:resource-server id="resourceServerFilter" resource-id="latifyApi" token-services-ref="tokenServices" />	
	
### Access Decision Manager

We also need to define an accessDecisionManager, capable of making a final access control (authorization) decision

This particular one requires  all voters to abstain or grant access.

	<bean id="accessDecisionManager" class="org.springframework.security.access.vote.UnanimousBased">
		<constructor-arg>
			<list>
				<bean class="org.springframework.security.oauth2.provider.vote.ScopeVoter" />
				<bean class="org.springframework.security.access.vote.RoleVoter" />
				<bean class="org.springframework.security.access.vote.AuthenticatedVoter" />
			</list>
		</constructor-arg>
	</bean>
	
### TokenService

A Spring provided default implementation of a token service, using random UUID values for the access token and refresh token values
	
- The tokenService delegates the persistence of the tokens to a `tokenStore`. 
- To access client specific details it uses a `clientDetailsService`.
	
	<bean id="tokenServices" class="org.springframework.security.oauth2.provider.token.DefaultTokenServices">
		<property name="tokenStore" ref="tokenStore" />
		<property name="supportRefreshToken" value="true" />
		<property name="clientDetailsService" ref="clientDetails" />
	</bean>	

### TokenStore

For this sample we're simply using an in-memory implementation.

	<bean id="tokenStore" class="org.springframework.security.oauth2.provider.token.InMemoryTokenStore" />

### Client Details Service

We start by creating a client details service that will list all of our OAuth clients

	<oauth:client-details-service id="clientDetails">
		<oauth:client client-id="my-trusted-client" authorized-grant-types="password,authorization_code,refresh_token,implicit"
			secret="somesecret"  authorities="ROLE_CLIENT, ROLE_TRUSTED_CLIENT" scope="read,write,trust" access-token-validity="60" />
	</oauth:client-details-service>
		
	
# Testing

## Current locations

### Retrieving current location
	curl --silent "${current_location_endpoint}"

## Update current location 

	curl --silent  -H "Content-Type: application/json" -d '{"latitude":1.0,"longitude":1.0}' -X POST ${current_location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"latitude":2.0,"longitude":2.0}' -X POST ${current_location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"latitude":3.0,"longitude":3.0}' -X POST ${current_location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"latitude":4.0,"longitude":4.0}' -X POST ${current_location_endpoint}

## Retrieving location history

	curl --silent "${location_endpoint}"
	curl --silent "${location_endpoint}/1378113390550"
	
	curl --silent "${location_endpoint}/?min-time=1378111961098&max-time=1378111963779"

## Add past locations


	

	curl --silent  -H "Content-Type: application/json" -d '{"accuracy":null,"altitude":null,"altitudeAccuracy":null,"heading":null,"latitude":10.0,"longitude":10.0,"speed":null,"timestampMs":1378112866695}' -X POST ${location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"accuracy":null,"altitude":null,"altitudeAccuracy":null,"heading":null,"latitude":20.0,"longitude":20.0,"speed":null,"timestampMs":1378112869730}' -X POST ${location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"accuracy":null,"altitude":null,"altitudeAccuracy":null,"heading":null,"latitude":30.0,"longitude":30.0,"speed":null,"timestampMs":1378112872770}' -X POST ${location_endpoint}
	sleep 1
	curl --silent  -H "Content-Type: application/json" -d '{"accuracy":null,"altitude":null,"altitudeAccuracy":null,"heading":null,"latitude":40.0,"longitude":40.0,"speed":null,"timestampMs":1378113390550}' -X POST ${location_endpoint}
	sleep 1


# Errors occured

- The request sent by the client was syntactically incorrect ().
(This was caused by invalid JSON being sent - attribute names were not encapsulated in quotes)
- The server refused this request because the request entity is in a format not supported by the requested resource for the requested method ().
- The resource identified by this request is only capable of generating responses with characteristics not acceptable according to the request "accept" headers ().

- An error occured when the oauth2 authorization was hit.

OAuth2 Authorization URL : 

	http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/authorize?client_id=my-trusted-client&redirect_uri=http://localhost:50577/Callback&response_type=code&scope=read%20write%20trust

Error

	org.springframework.security.authentication.InsufficientAuthenticationException: User must be authenticated with Spring Security before authorization can be completed.
		org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.authorize(AuthorizationEndpoint.java:145)
		sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
		sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
		sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
		java.lang.reflect.Method.invoke(Method.java:597)
		org.springframework.web.method.support.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:219)
		org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:132)
		org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:104)
		org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandleMethod(RequestMappingHandlerAdapter.java:745)
		org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:686)
		org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:80)
		org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:925)
		org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:856)
		org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:936)
		org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:827)
		javax.servlet.http.HttpServlet.service(HttpServlet.java:617)
		org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:812)
		javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
		org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:186)
		org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:160)
		org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:346)
		org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:259)

This means that we didn't enter the Oauth authorization flow with an authenticated user.
Keep in mind that as you are authorizating an application access to your data, you need to have 
a logged in user at this point.

Therefor, we need to provide a login page so that the user can login to the system if he didn't do so already.

In this sample, we're using a simple form based login.

	<http access-denied-page="/login.jsp?authorization_error=true" disable-url-rewriting="true"
		xmlns="http://www.springframework.org/schema/security">
		<intercept-url pattern="/oauth/**" access="ROLE_USER" />
		<intercept-url pattern="/**" access="IS_AUTHENTICATED_ANONYMOUSLY" />

		<form-login authentication-failure-url="/login.jsp?authentication_error=true" default-target-url="/index.jsp"
			login-page="/login.jsp" login-processing-url="/login.do" />
		<logout logout-success-url="/index.jsp" logout-url="/logout.do" />
		<anonymous />
	</http>


- After the user clicks authorize, we receive the authorization code but fail to swap it for a token.

This is because the oauth token url is not properly setup 

	http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/token
	
We need to have the following beans in place	
	
	<http pattern="/oauth/token" create-session="stateless" authentication-manager-ref="clientAuthenticationManager"
		xmlns="http://www.springframework.org/schema/security">
		<intercept-url pattern="/oauth/token" access="IS_AUTHENTICATED_FULLY" />
		<anonymous enabled="false" />
		<http-basic entry-point-ref="clientAuthenticationEntryPoint" />
		<!-- include this only if you need to authenticate clients via request parameters -->
		<custom-filter ref="clientCredentialsTokenEndpointFilter" after="BASIC_AUTH_FILTER" />
		<access-denied-handler ref="oauthAccessDeniedHandler" />
	</http>
	
	<bean id="clientAuthenticationEntryPoint" class="org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint">
		<property name="realmName" value="com.ecs.latify.api/client" />
		<property name="typeName" value="Basic" />
	</bean>
	
	<sec:authentication-manager id="clientAuthenticationManager">
		<sec:authentication-provider user-service-ref="clientDetailsUserService" />
	</sec:authentication-manager>
 		
	<bean id="clientCredentialsTokenEndpointFilter" class="org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter">
		<property name="authenticationManager" ref="clientAuthenticationManager" />
	</bean>
		

- No bean named 'org.springframework.security.authenticationManager' is defined

The following error occurs if you don't have an authenticaiton provider in your Spring Context.

	org.springframework.beans.factory.NoSuchBeanDefinitionException: 
	No bean named 'org.springframework.security.authenticationManager' is defined: 
	Did you forget to add a gobal <authentication-manager> element to your configuration 
	(with child <authentication-provider> elements)? 
	Alternatively you can use the authentication-manager-ref attribute on your <http> and <global-method-security> elements.

- Oauth2 authorization page not available

http://localhost:6002/com.ecs.samples.spring.simple.rest/oauth/authorize?client_id=my-trusted-client&redirect_uri=http://localhost:51122/Callback&response_type=code&scope=read%20write%20trust

You need to setup an authorization-server

	<oauth:authorization-server 
	    client-details-service-ref="clientDetails" 
	    token-services-ref="tokenServices"
		user-approval-handler-ref="userApprovalHandler">
		<oauth:authorization-code />
		<oauth:implicit />
		<oauth:refresh-token />
		<oauth:client-credentials />
		<oauth:password />
	</oauth:authorization-server>

- Insufficient scope

403 Forbidden
{"error":"insufficient_scope","error_description":"Insufficient scope for this resource","scope":"LOCATION"}

Make sure you provide the correct scopes...

# References

## Security

- [Spring OAuth 2 Developers Guide](https://github.com/SpringSource/spring-security-oauth/wiki/oAuth2)

# TODOs

- Remove in-mem-DB with a real DB
- Find out how the AccessDeniedHandler works and how it is triggered.