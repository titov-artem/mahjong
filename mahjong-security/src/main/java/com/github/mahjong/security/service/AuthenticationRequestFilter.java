package com.github.mahjong.security.service;

import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.security.service.model.AuthenticationType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class AuthenticationRequestFilter implements Filter {

    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";

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

        UsernamePasswordAuthenticationToken auth = null;

        // try to authorize via login and password
        Map<String, String[]> params = req.getParameterMap();
        String loginParam = getParam(LOGIN_PARAM, params);
        String passwordParam = getParam(PASSWORD_PARAM, params);
        if (!loginParam.isEmpty() && !passwordParam.isEmpty()) {
            auth = new UsernamePasswordAuthenticationToken(
                    loginParam,
                    passwordParam
            );
            auth.setDetails(AuthenticationType.LOGIN_PASSWORD);
        }

        // try to authorize via cookie
        if (auth == null && req instanceof HttpServletRequest) {
            Optional<AuthCookies> authCookiesOpt = AuthCookies.fromCookiesList(((HttpServletRequest) req).getCookies());
            if (authCookiesOpt.isPresent()) {
                auth = new UsernamePasswordAuthenticationToken(
                        authCookiesOpt.get().getLogin(),
                        authCookiesOpt.get().getData()
                );
                auth.setDetails(AuthenticationType.COOKIES);
            }
        }

        if (auth == null) {
            auth = new UsernamePasswordAuthenticationToken("", "");
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getParam(String name, Map<String, String[]> params) {
        if (params.isEmpty()) {
            return "";
        }
        if (!params.containsKey(name)) {
            return "";
        }
        String value = params.get(name)[0];
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public void destroy() {

    }

}
