package com.github.mahjong.security.controller.impl;

import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.common.security.api.rest.UserController;
import com.github.mahjong.common.security.api.rest.dto.MahjongUserDto;
import com.github.mahjong.security.model.MahjongUser;
import com.github.mahjong.security.repo.UserRepo;
import com.github.mahjong.security.service.CookieService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller("userController")
public class UserControllerImpl implements UserController {

    private final CookieService cookieService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Inject
    public UserControllerImpl(CookieService cookieService,
                              PasswordEncoder passwordEncoder,
                              UserRepo userRepo) {
        this.cookieService = cookieService;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Override
    public Response createUser(HttpServletRequest request, MahjongUserDto user) throws ServletException {
        Optional<MahjongUser> existingUser = userRepo.get(user.login);
        if (existingUser.isPresent()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        userRepo.create(new MahjongUser(user.login, passwordEncoder.encode(user.password)));
        request.logout();
        return Response.noContent()
                .build();
    }

    @Override
    public Response authenticate(Cookie loginCookie, Cookie dataCookie, String login, String password) {
        // All authentication logic will be done via authentication filter.
        // We have to setup cookie to enable authentication on other mahjong services
        // or refresh it.
        AuthCookies generatedCookie = cookieService.refreshAuthCookie(currentUsername());
        return Response.noContent()
                .cookie(generatedCookie.toCookie(TimeUnit.DAYS.toSeconds(CookieService.COOKIE_EXPIRATION_PERIOD_DAYS)))
                .build();
    }

    @Override
    public String current() {
        return currentUsername();
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }

    @Override
    public void logout() {
        // All logic will be done via authentication filter.
    }


}
