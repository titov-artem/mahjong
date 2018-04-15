package com.github.mahjong.common.security.api.model;

import lombok.Data;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.NewCookie;
import java.util.Date;
import java.util.Optional;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;
import static javax.ws.rs.core.NewCookie.DEFAULT_MAX_AGE;

@Data
public class AuthCookies {
    public static final String LOGIN_COOKIE_NAME = "login";
    public static final String DATA_COOKIE_NAME = "data";

    private final String login;
    private final String data;

    public static Optional<AuthCookies> fromCookiesList(@Nullable Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }
        String login = null;
        String data = null;
        for (Cookie c : cookies) {
            if (LOGIN_COOKIE_NAME.equals(c.getName())) {
                login = c.getValue();
            }
            if (DATA_COOKIE_NAME.equals(c.getName())) {
                data = c.getValue();
            }
        }
        if (login == null || data == null) {
            return Optional.empty();
        }
        return Optional.of(new AuthCookies(login, data));
    }

    /**
     * @param liveTime cookie live time in seconds. 0 - means remove, -1 - means remove at the end of
     *                 the browser session
     * @return auth cookies converted to array of cookies
     */
    public NewCookie[] toCookie(long liveTime) {
        Date expiry = liveTime == 0 ? new Date(0) : null;
        return new NewCookie[]{
                new NewCookie(LOGIN_COOKIE_NAME, login, "/", null, DEFAULT_VERSION, null, DEFAULT_MAX_AGE, expiry, false, false),
                new NewCookie(DATA_COOKIE_NAME, data, "/", null, DEFAULT_VERSION, null, DEFAULT_MAX_AGE, expiry, false, false)
        };
    }
}
