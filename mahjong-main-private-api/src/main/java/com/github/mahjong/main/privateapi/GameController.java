package com.github.mahjong.main.privateapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.main.privateapi.dto.GameStartForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;

@Path("/game")
public interface GameController extends REST {

    @POST
    GameView startGame(@NotNull @Valid GameStartForm form);

    // we use post here to be able to pass big set of ids
    @Path("/batch")
    @POST
    List<GameView> getAll(Set<Long> gameIds);
}
