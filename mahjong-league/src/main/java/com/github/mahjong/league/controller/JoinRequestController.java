package com.github.mahjong.league.controller;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.league.controller.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/join/request")
public interface JoinRequestController extends REST {

    /**
     * @return list of join requests from current user
     */
    @Path("/outgoing")
    @GET
    List<JoinRequestView> getAllIngoing();


    /**
     * @return list of join request to specified league
     */
    @Path("/league/{id}/ingoing")
    @GET
    List<JoinRequestView> getAllIngoing(@PathParam("id") @NotNull Long leagueId);

    @POST
    JoinRequestView create(@NotNull @Valid JoinRequestForm form);

    @Path("/{id}/approve")
    @POST
    void approve(@PathParam("id") @NotNull Long id);

    @Path("{id}/reject")
    @POST
    void reject(@PathParam("id") @NotNull Long id, @NotNull @Valid JoinRequestRejectForm form);

}
