package com.github.mahjong.application.server;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.util.EnumSet;

public class JettyServer extends Server {

    private final int port;
    private final SpringBus bus;
    private final Filter securityFilter;

    public JettyServer(SpringBus bus, int port, Filter securityFilter) {
        super(port);
        this.port = port;
        this.bus = bus;
        this.securityFilter = securityFilter;
    }

    @PostConstruct
    public void init() throws ServletException {
        // Configure handlers.
        // Create CXF servlet.
        CXFNonSpringServlet servlet = new CXFNonSpringServlet();
        servlet.setBus(bus);
        servlet.init();

        // Create servlet context handler for CXF servlet.
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder("cxf", servlet), "/services/*");
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setSessionHandler(new SessionHandler());
        servletContextHandler.setContextPath("/");
        servletContextHandler.setServletHandler(servletHandler);

        // Add Spring Security Filter by the name
        servletContextHandler.addFilter(
                new FilterHolder(new DelegatingFilterProxy(securityFilter)),
                "/*", EnumSet.allOf(DispatcherType.class)
        );

        // Builder jetty hadlers list.
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(servletContextHandler);
        handlers.addHandler(new DefaultHandler());
        setHandler(handlers);

        // Configure server properties.
        this.setStopAtShutdown(true);
    }
}
