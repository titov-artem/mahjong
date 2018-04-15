package com.github.mahjong.league.controller;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.main.privateapi.dto.GameStartForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;

@Path("/game")
public interface LeagueGameController extends REST {

    @GET
    List<GameView> getAll(@QueryParam("leagueId") @NotNull Long leagueId);

    @GET
    List<GameView> getActive(@QueryParam("leagueId") @NotNull Long leagueId);

    @Path("/league/{id}")
    @POST
    GameView start(@PathParam("id") @NotNull Long leagueId,
                   @NotNull @Valid GameStartForm form);

}
