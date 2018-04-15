package com.github.mahjong.main.app;

import com.github.mahjong.application.server.ServerEndpointFactory;
import com.github.mahjong.main.publicapi.EnumsController;
import com.github.mahjong.main.publicapi.GameController;
import com.github.mahjong.main.publicapi.PlayerController;
import com.github.mahjong.main.publicapi.RulesController;
import org.apache.cxf.endpoint.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Arrays;

@Configuration
public class PublicApiEndpointConfig {

    private final ServerEndpointFactory endpointFactory;

    @Inject
    public PublicApiEndpointConfig(ServerEndpointFactory endpointFactory) {
        this.endpointFactory = endpointFactory;
    }

    @Bean
    public Server publicApiEndpoint(PlayerController playerController,
                                    GameController gameController,
                                    RulesController rulesController,
                                    EnumsController enumsController,
                                    UserContextFillerInterceptor userContextFillerInterceptor) {
        return endpointFactory.createEndpoint(
                "/api/main",
                Arrays.asList(
                        userContextFillerInterceptor
                ),
                Arrays.asList(
                        playerController,
                        gameController,
                        rulesController,
                        enumsController
                )
        );
    }
}
