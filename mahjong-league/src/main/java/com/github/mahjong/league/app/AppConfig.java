package com.github.mahjong.league.app;

import com.github.mahjong.application.context.ThreadLocalUserContextSupport;
import com.github.mahjong.application.server.ServerConfig;
import com.github.mahjong.common.security.api.config.SecurityConfig;
import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.common.security.api.model.PathSecurityRestriction;
import com.github.mahjong.league.service.PlayerCacheService;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

@Configuration
@Import({
        SecurityConfig.class,
        JdbcConfig.class,
        ServerConfig.class,
        ApiEndpointConfig.class,
        ThirdPartyServicesConfig.class
})
@ComponentScan(basePackages = "com.github.mahjong.league")
@PropertySource({
        "classpath:context/application.properties"
})
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor() {
        return new CommonAnnotationBeanPostProcessor();
    }

    @Bean
    public RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor() {
        return new RequiredAnnotationBeanPostProcessor();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public List<PathSecurityRestriction> getSecurityRestrictions() {
        return Arrays.asList(
                PathSecurityRestriction.hasRole("/services/api/league/**", MahjongUserRole.USER.name())
        );
    }

    @Bean
    public ThreadLocalUserContextSupport userContextSupport() {
        return new ThreadLocalUserContextSupport();
    }

    @Bean
    public UserContextFillerInterceptor userContextFillerFilter(PlayerCacheService playerCacheService,
                                                                ThreadLocalUserContextSupport contextSupport) {
        return new UserContextFillerInterceptor(playerCacheService, contextSupport);
    }
}
