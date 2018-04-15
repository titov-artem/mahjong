package com.github.mahjong.main.controller.privateapi.impl;

import com.github.mahjong.main.controller.privateapi.dto.PlayerDtoHelper;
import com.github.mahjong.main.privateapi.PlayerController;
import com.github.mahjong.main.privateapi.dto.PlayerDto;
import com.github.mahjong.main.repo.PlayerRepo;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Controller("privatePlayerController")
public class PlayerControllerImpl implements PlayerController {

    private final PlayerRepo playerRepo;

    @Inject
    public PlayerControllerImpl(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    @Override
    public PlayerDto get(Long id) {
        return playerRepo.get(id).map(PlayerDtoHelper::from)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public PlayerDto getByLogin(String login) {
        return playerRepo.getByLogin(login).map(PlayerDtoHelper::from)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<PlayerDto> getAll(Set<Long> ids) {
        return playerRepo.getAll(ids).stream()
                .map(PlayerDtoHelper::from)
                .collect(toList());
    }
}
