package com.ecs.samples.spring.simple.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.ecs.samples.spring.simple.rest.model.OAuthAccessToken;

public interface OAuthAccessTokenRepository extends CrudRepository<OAuthAccessToken, String> {

}
