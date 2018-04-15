package com.github.mahjong.main.app;

import com.github.mahjong.application.context.ThreadLocalUserContextSupport;
import com.github.mahjong.application.server.ServerConfig;
import com.github.mahjong.common.security.api.config.SecurityConfig;
import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.common.security.api.model.PathSecurityRestriction;
import com.github.mahjong.main.repo.PlayerRepo;
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
        PublicApiEndpointConfig.class,
        PrivateApiEndpointConfig.class,
})
@ComponentScan(basePackages = "com.github.mahjong.main")
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
                new PathSecurityRestriction("/services/api/main/enums/**", "permitAll"),
                PathSecurityRestriction.hasRole("/services/api/main/player/**", MahjongUserRole.USER.name()),
                PathSecurityRestriction.hasRole("/services/api/main/game/**", MahjongUserRole.USER.name()),
                PathSecurityRestriction.hasRole("/services/api/main/rules/**", MahjongUserRole.USER.name())
        );
    }

    @Bean
    public ThreadLocalUserContextSupport userContextSupport() {
        return new ThreadLocalUserContextSupport();
    }

    @Bean
    public UserContextFillerInterceptor userContextFillerFilter(PlayerRepo playerRepo,
                                                                ThreadLocalUserContextSupport contextSupport) {
        return new UserContextFillerInterceptor(playerRepo, contextSupport);
    }
}
