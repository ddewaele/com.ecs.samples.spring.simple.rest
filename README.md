#Introduction

Simple project to bootstrap a Spring MVC REST API.

CLone the project, execute `mvn tomcat:run` and go to the following URL:

http://localhost:6002/com.ecs.samples.spring.simple.rest/helloWorld

# Setup

## Environment variables

### Local setup
	
	export host=localhost
	export port=6002
	export location_endpoint=http://${host}:${port}/com.ecs.samples.spring.simple.rest/location
	export current_location_endpoint=http://${host}:${port}/com.ecs.samples.spring.simple.rest/currentLocation

# Changes 

## Spring context

## Annotation and component scanning

- Added `<context:annotation-config />` allowing us to activate annotations in beans already registered in the application context (no matter if they were defined with XML or by package scanning).
- Added `<context:component-scan>` to scan packages to find and register beans within the application context.
- Added `<mvc:annotation-driven>` to support for new Spring MVC features such as declarative validation with @Valid, HTTP message conversion with @RequestBody/@ResponseBody, new field conversion architecture, etc
- Added `<tx:annotation-driven>` to enable the transactional behavior based on annotations.


##EntityManagerFactory

The simplest way to create an EntityManagerFactory bean is to define it like this:
	
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

## The persistence.xml

This is added in the META-INF folder of the package that holds the Entity classes.

	<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
		<persistence-unit name="spring-jpa">
			<provider>org.hibernate.ejb.HibernatePersistence</provider>
		</persistence-unit>
	</persistence>


## The transactionManager

	<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
			<property name="entityManagerFactory" ref="entityManagerFactory" />
	</bean>
    
## The datasource

	<jdbc:embedded-database id="dataSource" type="HSQL" />    
	
	
# OAuth2

## Spring Context setup

### Protected URLS

	<http pattern="/locations/**" create-session="never" entry-point-ref="oauthAuthenticationEntryPoint"
		access-decision-manager-ref="accessDecisionManager" xmlns="http://www.springframework.org/schema/security">
		<anonymous enabled="false" />
		<intercept-url pattern="/locations" access="ROLE_USER,SCOPE_READ" />
		<custom-filter ref="resourceServerFilter" before="PRE_AUTH_FILTER" />
		<access-denied-handler ref="oauthAccessDeniedHandler" />
	</http>
	

In order for the protective urls to work we need to define a OAuth2AuthenticationEntryPoint. This is capable of delivering a reaml in case of an unauthorized error.
That way the client will be informed.
	
	<bean id="oauthAuthenticationEntryPoint" class="org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint">
		<property name="realmName" value="sparklr2" />
	</bean>
	

We also need to define an accessDecisionManager, capable of making a final access control (authorization) decision

	<bean id="accessDecisionManager" class="org.springframework.security.access.vote.UnanimousBased" xmlns="http://www.springframework.org/schema/beans">
		<constructor-arg>
			<list>
				<bean class="org.springframework.security.oauth2.provider.vote.ScopeVoter" />
				<bean class="org.springframework.security.access.vote.RoleVoter" />
				<bean class="org.springframework.security.access.vote.AuthenticatedVoter" />
			</list>
		</constructor-arg>
	</bean>



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


