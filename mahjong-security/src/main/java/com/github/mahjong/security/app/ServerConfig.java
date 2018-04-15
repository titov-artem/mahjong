package com.github.mahjong.security.app;

import com.github.mahjong.security.server.JettyServer;
import com.github.mahjong.security.server.JettyStarter;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class ServerConfig {

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public JettyServer jettyServer(@Qualifier("cxf") SpringBus cxf,
                                   @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {
        return new JettyServer(cxf, port, springSecurityFilterChain);
    }

    @Bean
    public JettyStarter jettyStarter(JettyServer jettyServer) {
        return new JettyStarter(jettyServer);
    }

}
