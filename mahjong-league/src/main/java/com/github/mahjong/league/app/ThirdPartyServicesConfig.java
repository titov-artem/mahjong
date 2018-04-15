package com.github.mahjong.league.app;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.mahjong.common.rest.providers.JSR330ParamConverterProvider;
import com.github.mahjong.common.rest.providers.ObjectMapperContextResolver;
import com.github.mahjong.main.privateapi.GameController;
import com.github.mahjong.main.privateapi.PlayerController;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ThirdPartyServicesConfig {

    private static final List<Object> JAX_RS_PROVIDERS = Arrays.asList(
            new JacksonJsonProvider(),
            new JSR330ParamConverterProvider(),
            new ObjectMapperContextResolver()
    );

    private final String mainPrivateApiUrl;

    @Inject
    public ThirdPartyServicesConfig(@Value("${league.main.private.api.url:http://localhost:8081/services/api/private/main}") String mainPrivateApiUrl) {
        this.mainPrivateApiUrl = mainPrivateApiUrl;
    }

    @Bean
    public PlayerController playerController() {
        return JAXRSClientFactory.create(mainPrivateApiUrl, PlayerController.class, JAX_RS_PROVIDERS, true);
    }

    @Bean
    public GameController gameController() {
        return JAXRSClientFactory.create(mainPrivateApiUrl, GameController.class, JAX_RS_PROVIDERS, true);
    }
}
