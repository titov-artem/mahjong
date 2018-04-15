package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.api.common.dto.PlayerShortView;
import com.github.mahjong.common.enums.EnumUtils;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.exceptions.PlayerLoginNotFoundException;
import com.github.mahjong.main.exceptions.PlayerNotFoundException;
import com.github.mahjong.main.controller.dto.PlayerShortViewHelper;
import com.github.mahjong.main.controller.publicapi.dto.PlayerDtoHelper;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.publicapi.PlayerController;
import com.github.mahjong.main.publicapi.dto.PlayerDto;
import com.github.mahjong.main.repo.PlayerRepo;
import com.github.mahjong.main.service.PlayerService;
import com.google.common.base.Preconditions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Controller("publicPlayerController")
public class PlayerControllerImpl extends AbstractPlayerAwareController implements PlayerController {

    private final PlayerService playerService;

    @Inject
    public PlayerControllerImpl(PlayerService playerService, PlayerRepo playerRepo) {
        super(playerRepo);
        this.playerService = playerService;
    }

    @Override
    public PlayerDto create(PlayerDto player) {
        Preconditions.checkArgument(player.id == null, "Can't create player with specified id");
        Player registered = playerService.registerPlayer(player.login,
                player.name,
                EnumUtils.transferClass(player.lang, LangIso639.class)
        );
        return PlayerDtoHelper.from(registered);
    }

    @Override
    public List<PlayerShortView> getPlayers() {
        return playerRepo.getAll().stream().map(PlayerShortViewHelper::from).collect(toList());
    }

    @Override
    public PlayerDto currentPlayer() {
        return PlayerDtoHelper.from(getCurrentPlayer());
    }

    @Override
    public PlayerShortView getPlayer(Long id) {
        Player player = playerRepo.get(id)
                .orElseThrow(PlayerNotFoundException.supplier(id));
        return PlayerShortViewHelper.from(player);
    }

    @Override
    public PlayerDto update(Long id, PlayerDto playerDto) {
        Preconditions.checkArgument(Objects.equals(id, playerDto.id),
                "Can't update player with id %s by path with id %s", playerDto.id, id);
        Optional<Player> player = playerRepo.update(PlayerDtoHelper.toPlayer(playerDto));
        if (!player.isPresent()) {
            throw new PlayerNotFoundException(id);
        }
        return PlayerDtoHelper.from(player.get());
    }

    @Override
    public Response delete(Long id) {
        Optional<Player> player = playerRepo.get(id);
        if (!player.isPresent()) {
            throw new PlayerNotFoundException(id);
        }
        playerRepo.delete(player.get());
        return Response.noContent().build();
    }

}
