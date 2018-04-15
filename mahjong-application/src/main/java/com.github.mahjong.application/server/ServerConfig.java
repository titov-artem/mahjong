package com.github.mahjong.application.server;

import com.github.mahjong.application.exceptions.ServerExceptionMapper;
import com.github.mahjong.application.context.UserContextSupport;
import com.github.mahjong.common.translation.DefaultTranslationMessageSource;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.inject.Inject;
import javax.servlet.Filter;

@Configuration
@ImportResource({
        "classpath:META-INF/cxf/cxf.xml",
        "classpath:META-INF/cxf/cxf-servlet.xml",
})
public class ServerConfig {

    private final int port;

    @Inject
    public ServerConfig(@Value("${server.port:8081}") int port) {
        this.port = port;
    }

    @Bean
    public JettyServer jettyServer(@Qualifier("cxf") SpringBus cxf,
                                   @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {
        return new JettyServer(cxf, port, springSecurityFilterChain);
    }

    @Bean
    public JettyStarter jettyStarter(JettyServer jettyServer) {
        return new JettyStarter(jettyServer);
    }

    @Bean
    public ServerExceptionMapper serverExceptionMapper(UserContextSupport userContextSupport) {
        return new ServerExceptionMapper(
                new DefaultTranslationMessageSource("exception/messages"),
                userContextSupport
        );
    }

    @Bean
    public ServerEndpointFactory endpointFactory(@Qualifier("cxf") SpringBus cxf,
                                                 ServerExceptionMapper exceptionMapper) {
        return new ServerEndpointFactory(cxf, exceptionMapper);

    }

}
