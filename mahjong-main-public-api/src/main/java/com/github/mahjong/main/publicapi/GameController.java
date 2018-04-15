package com.github.mahjong.main.publicapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.main.publicapi.dto.RoundScoreDto;
import org.apache.cxf.jaxrs.ext.PATCH;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/game")
public interface GameController extends REST {

    @Path("/active")
    @GET
    List<GameView> getAllActive();

    @Path("/{id}")
    @GET
    GameView get(@PathParam("id") @NotNull Long id);

    @Path("/{id}")
    @PATCH
    GameView roundComplete(@PathParam("id") @NotNull Long id, @NotNull @Valid RoundScoreDto score);
}
