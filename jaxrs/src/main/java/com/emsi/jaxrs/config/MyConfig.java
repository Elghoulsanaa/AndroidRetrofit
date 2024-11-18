package com.emsi.jaxrs.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    @Bean
    public ResourceConfig resourceConfig() {
        ResourceConfig jerseyServlet = new ResourceConfig();
        jerseyServlet.register(com.emsi.jaxrs.rest.CompteRestJaxRSAPI.class); 
        return jerseyServlet;
    }
}