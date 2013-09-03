package com.ecs.samples.spring.simple.rest.repository;

import org.springframework.data.repository.CrudRepository;

import com.ecs.samples.spring.simple.rest.model.OAuthRefreshToken;

public interface OAuthRefreshTokenRepository extends CrudRepository<OAuthRefreshToken, String> {

}
