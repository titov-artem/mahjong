package com.github.mahjong.common.security.api.service;

import com.github.mahjong.common.security.api.model.AuthCookies;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationRequestFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
        fillAuthentication(req);
        fc.doFilter(req, res);
    }

    private void fillAuthentication(ServletRequest req) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            return;
        }

        if (req instanceof HttpServletRequest) {
            Optional<AuthCookies> authCookiesOpt = AuthCookies.fromCookiesList(((HttpServletRequest) req).getCookies());
            if (authCookiesOpt.isPresent()) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        authCookiesOpt.get().getLogin(),
                        authCookiesOpt.get().getData()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

    }

    @Override
    public void destroy() {

    }

}
