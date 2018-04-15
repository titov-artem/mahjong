package com.github.mahjong.security.service;

import com.github.mahjong.common.security.api.model.AuthCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppSecurityConfigurer extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(AppSecurityConfigurer.class);

    private final AuthenticationManagerBuilder auth;
    private final RequestAuthenticationProvider authenticationProvider;

    @Inject
    public AppSecurityConfigurer(AuthenticationManagerBuilder auth,
                                 RequestAuthenticationProvider authenticationProvider) {
        this.auth = auth;
        this.authenticationProvider = authenticationProvider;
    }

    @PostConstruct
    public void configureGlobal() throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authenticationProvider(authenticationProvider)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
                .authorizeRequests()
                .antMatchers("/services/api/auth/user/create").access("hasRole('ANONYMOUS')")
                .antMatchers("/services/api/auth/user/authenticate").access("hasRole('USER')")
                .antMatchers("/services/api/auth/user/current").access("hasRole('USER')")
                .antMatchers("/services/api/auth/user/logout").access("hasRole('USER')")
                .and()
                .addFilterBefore(new AuthenticationRequestFilter(), BasicAuthenticationFilter.class)
                .logout()
                .logoutUrl("/services/api/auth/user/logout")
                .addLogoutHandler(new CookieClearingLogoutHandler(AuthCookies.LOGIN_COOKIE_NAME, AuthCookies.DATA_COOKIE_NAME))
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT));
    }
}

