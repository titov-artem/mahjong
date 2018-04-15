package com.github.mahjong.main.publicapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.main.publicapi.dto.PlayerDto;
import com.github.mahjong.api.common.dto.PlayerShortView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/player")
public interface PlayerController extends REST {

    @POST
    PlayerDto create(@NotNull @Valid PlayerDto player);

    @GET
    List<PlayerShortView> getPlayers();

    @Path("/current")
    @GET
    PlayerDto currentPlayer();

    @Path("/{id}")
    @GET
    PlayerShortView getPlayer(@PathParam("id") @NotNull Long id);

    @Path("/{id}")
    @PUT
    PlayerDto update(@PathParam("id") @NotNull Long id, @NotNull @Valid PlayerDto playerDto);

    @Path("/{id}")
    @DELETE
    Response delete(@PathParam("id") @NotNull Long id);

}
