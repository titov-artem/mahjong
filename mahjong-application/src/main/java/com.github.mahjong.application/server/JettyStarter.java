package com.github.mahjong.application.server;

import org.eclipse.jetty.server.Server;

import javax.annotation.PostConstruct;

/**
 * @author scorpion@yandex-team on 14.04.15.
 */
public class JettyStarter {
    private final Server jettyServer;

    public JettyStarter(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    @PostConstruct
    public void start() throws Exception {
        jettyServer.start();
    }

}
