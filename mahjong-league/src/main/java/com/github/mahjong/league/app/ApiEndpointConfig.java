package com.github.mahjong.league.app;

import com.github.mahjong.application.server.ServerEndpointFactory;
import com.github.mahjong.league.controller.*;
import org.apache.cxf.endpoint.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Arrays;

@Configuration
public class ApiEndpointConfig {

    private final ServerEndpointFactory endpointFactory;

    @Inject
    public ApiEndpointConfig(ServerEndpointFactory endpointFactory) {
        this.endpointFactory = endpointFactory;
    }

    @Bean
    public Server apiEndpoint(LeagueController leagueController,
                              LeagueGameController leagueGameController,
                              LeaguePlayerController leaguePlayerController,
                              InvitationController invitationController,
                              JoinRequestController joinRequestController,
                              UserContextFillerInterceptor userContextFillerInterceptor) {
        return endpointFactory.createEndpoint(
                "/api/league",
                Arrays.asList(
                        userContextFillerInterceptor
                ),
                Arrays.asList(
                        leagueController,
                        leagueGameController,
                        leaguePlayerController,
                        invitationController,
                        joinRequestController
                )
        );
    }

}
