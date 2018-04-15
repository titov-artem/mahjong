package com.github.mahjong.common.security.api.config;

import com.github.mahjong.common.security.api.rest.UserController;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@ComponentScan("com.github.mahjong.common.security.api")
public class SecurityConfig {

    @Value("${security.user.contoller.url:http://localhost:8080/services/api/auth}")
    private String userControllerUrl;

    @Bean
    public UserController userController() {
        return JAXRSClientFactory.create(userControllerUrl, UserController.class, Collections.emptyList(), true);
    }

}
