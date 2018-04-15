package com.github.mahjong.application.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.mahjong.application.exceptions.ServerExceptionMapper;
import com.github.mahjong.common.rest.providers.JSR330ParamConverterProvider;
import com.github.mahjong.common.rest.providers.ObjectMapperContextResolver;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.message.Message;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ServerEndpointFactory {

    private final SpringBus cxf;
    private final ServerExceptionMapper exceptionMapper;

    public ServerEndpointFactory(SpringBus cxf, ServerExceptionMapper exceptionMapper) {
        this.cxf = cxf;
        this.exceptionMapper = exceptionMapper;
    }

    public Server createEndpoint(String address,
                                 List<Object> services) {
        return createEndpoint(address, Collections.emptyList(), services);
    }

    public Server createEndpoint(String address,
                                 Collection<Interceptor<? extends Message>> inInterceptors,
                                 List<Object> services) {
        JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
        serverFactory.setProviders(Arrays.asList(
                new JacksonJsonProvider(),
                new ObjectMapperContextResolver(),
                new JSR330ParamConverterProvider(),
                exceptionMapper
        ));
        serverFactory.setBus(cxf);
        serverFactory.setAddress(address);
        serverFactory.getInInterceptors().addAll(inInterceptors);
        serverFactory.getInInterceptors().add(new JAXRSBeanValidationInInterceptor());
        serverFactory.setServiceBeans(services);
        return serverFactory.create();

    }

}
