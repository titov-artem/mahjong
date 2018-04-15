package com.github.mahjong.main.privateapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.main.privateapi.dto.PlayerDto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Set;

@Path("/player")
public interface PlayerController extends REST {

    @Path("/{id}")
    @GET
    PlayerDto get(@PathParam("id") @NotNull Long id);

    @Path("/login/{login}")
    @GET
    PlayerDto getByLogin(@PathParam("login") @NotNull String login);

    @Path("/batch")
    @POST
    List<PlayerDto> getAll(@NotNull Set<Long> ids);

}
