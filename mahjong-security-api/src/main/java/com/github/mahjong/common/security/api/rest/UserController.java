package com.github.mahjong.common.security.api.rest;


import com.github.mahjong.common.security.api.model.AuthCookies;
import com.github.mahjong.common.security.api.rest.dto.MahjongUserDto;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/user")
public interface UserController {

    @Path("/create")
    @POST
    Response createUser(@Context HttpServletRequest request, @Valid MahjongUserDto user) throws ServletException;

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/authenticate")
    @POST
    Response authenticate(@CookieParam(AuthCookies.LOGIN_COOKIE_NAME) Cookie loginCookie,
                          @CookieParam(AuthCookies.DATA_COOKIE_NAME) Cookie dataCookie,
                          @FormParam("login") @Nullable String login,
                          @FormParam("password") @Nullable String password);

    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/current")
    @GET
    String current();

    @Consumes(MediaType.WILDCARD)
    @Path("/logout")
    @POST
    void logout();

}
