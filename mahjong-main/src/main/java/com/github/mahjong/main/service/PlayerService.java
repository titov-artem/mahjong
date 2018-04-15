package com.github.mahjong.main.service;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.repo.PlayerRepo;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Service
public class PlayerService {

    private final PlayerRepo playerRepo;

    @Inject
    public PlayerService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Player registerPlayer(String login, String name, LangIso639 lang) {
        Player player = new Player(-1, login, name, lang);
        // todo add some captcha here in future
        return playerRepo.create(player);
    }

}
