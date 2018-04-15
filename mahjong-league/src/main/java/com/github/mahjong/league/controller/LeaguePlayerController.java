package com.github.mahjong.league.controller;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.api.common.dto.PlayerShortView;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/player")
public interface LeaguePlayerController extends REST {

    @GET
    List<PlayerShortView> getAll(@QueryParam("leagueId") @NotNull Long leagueId);

}
