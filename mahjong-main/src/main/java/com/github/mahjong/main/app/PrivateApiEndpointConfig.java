package com.github.mahjong.main.app;

import com.github.mahjong.application.server.ServerEndpointFactory;
import com.github.mahjong.main.privateapi.GameController;
import com.github.mahjong.main.privateapi.PlayerController;
import org.apache.cxf.endpoint.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Arrays;

@Configuration
public class PrivateApiEndpointConfig {

    private final ServerEndpointFactory endpointFactory;

    @Inject
    public PrivateApiEndpointConfig(ServerEndpointFactory endpointFactory) {
        this.endpointFactory = endpointFactory;
    }

    @Bean
    public Server privateApiEndpoint(PlayerController playerController,
                                     GameController gameController) {
        return endpointFactory.createEndpoint(
                "/api/private/main",
                Arrays.asList(
                        playerController,
                        gameController
                )
        );
    }
}
