package com.github.mahjong.league.controller;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.league.controller.dto.LeagueForm;
import com.github.mahjong.league.controller.dto.LeagueView;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;
import java.util.Set;

@Path("/league")
public interface LeagueController extends REST {

    /**
     * @return list of all existing leagues
     */
    @GET
    List<LeagueView> getAll();

    /**
     * @return list of leagues joined by current user
     */
    @Path("/joined")
    @GET
    List<LeagueView> getAllJoined();

    /**
     * @return list of leagues admined by current user
     */
    @Path("/admin")
    @GET
    List<LeagueView> getAllAdmined();

    @POST
    LeagueView create(@NotNull @Valid LeagueForm form);

    @Path("/{id}")
    @GET
    LeagueView get(@PathParam("id") @NotNull Long id);

    @Path("/{id}/admin")
    @POST
    LeagueView addAdmin(@PathParam("id") @NotNull Long id, @NotNull @NotEmpty Set<Long> admins);

    @Path("/{id}/admin")
    @DELETE
    LeagueView removeAdmin(@PathParam("id") @NotNull Long id, @NotNull @NotEmpty Set<Long> admins);

}
