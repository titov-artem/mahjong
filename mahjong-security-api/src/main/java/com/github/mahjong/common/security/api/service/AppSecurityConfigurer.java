package com.github.mahjong.common.security.api.service;

import com.github.mahjong.common.security.api.model.PathSecurityRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AppSecurityConfigurer extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(AppSecurityConfigurer.class);

    private final RequestAuthenticationProvider authenticationProvider;
    private final AuthenticationManagerBuilder auth;
    private final List<PathSecurityRestriction> restrictions;

    @Inject
    public AppSecurityConfigurer(RequestAuthenticationProvider authenticationProvider,
                                 AuthenticationManagerBuilder auth,
                                 List<PathSecurityRestriction> restrictions) {
        this.authenticationProvider = authenticationProvider;
        this.auth = auth;
        this.restrictions = restrictions;
    }

    @PostConstruct
    public void configureGlobal() throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http
                .csrf().disable()
                .authenticationProvider(authenticationProvider)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
                .authorizeRequests();
        for (PathSecurityRestriction restriction : restrictions) {
            expressionInterceptUrlRegistry = expressionInterceptUrlRegistry
                    .antMatchers(restriction.getPathExpression()).access(restriction.getRequiredAccessFilter());
        }
        expressionInterceptUrlRegistry
                .and()
                .addFilterBefore(new AuthenticationRequestFilter(), BasicAuthenticationFilter.class)
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT));
    }
}

