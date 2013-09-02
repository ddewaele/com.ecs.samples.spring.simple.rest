package com.ecs.samples.spring.simple.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-servlet.xml" })
@WebAppConfiguration
public class SimpleControllerTest {
 
    @Autowired private WebApplicationContext ctx;
 
    private MockMvc mockMvc;
 
    @Before public void setUp() {
        this.mockMvc = webAppContextSetup(ctx).build();
    }
 
    @Test public void updateCurrentLocation() throws Exception {
        mockMvc.perform(post("/currentLocation").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{\"latitude\":1.0,\"longitude\":1.0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("{\"accuracy\":null,\"altitude\":null,\"altitudeAccuracy\":null,\"heading\":null,\"latitude\":1.0,\"longitude\":1.0,\"speed\":null,\"timestampMs\":")));
    }
    
    @Test public void retrieveEmptyCurrentLocation() throws Exception {
        mockMvc.perform(get("/currentLocation").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"accuracy\":null,\"altitude\":null,\"altitudeAccuracy\":null,\"heading\":null,\"latitude\":null,\"longitude\":null,\"speed\":null,\"timestampMs\":null}"));
    }
    
    
    
}