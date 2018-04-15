package com.github.mahjong.security.app;

import com.github.mahjong.common.security.api.model.PathSecurityRestriction;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.time.Clock;
import java.util.Collections;
import java.util.List;

@Configuration
@Import({
        SecurityConfig.class,
        JdbcConfig.class,
        ServerConfig.class
})
@ComponentScan("com.github.mahjong.security")
@ImportResource({"classpath*:/context/application-ctx.xml"})
@PropertySource({
        "classpath:context/application.properties"
})
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public List<PathSecurityRestriction> getSecurityRestrictions() {
        return Collections.emptyList();
    }
}
