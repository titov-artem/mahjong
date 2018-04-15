package com.github.mahjong.security.server;

import org.eclipse.jetty.server.Server;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author scorpion@yandex-team on 14.04.15.
 */
public class JettyStarter {

    private final Server jettyServer;

    @Inject
    public JettyStarter(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    @PostConstruct
    public void start() throws Exception {
        jettyServer.start();
    }

}
