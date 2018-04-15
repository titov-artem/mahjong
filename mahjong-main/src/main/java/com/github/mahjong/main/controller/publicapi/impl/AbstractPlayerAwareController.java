package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.main.exceptions.PlayerLoginNotFoundException;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.repo.PlayerRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

abstract class AbstractPlayerAwareController {

    protected final PlayerRepo playerRepo;

    protected AbstractPlayerAwareController(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    protected final Player getCurrentPlayer() {
        String currentPlayerLogin = getCurrentPlayerLogin();
        return playerRepo.getByLogin(currentPlayerLogin)
                .orElseThrow(PlayerLoginNotFoundException.supplier(currentPlayerLogin));
    }

    protected final String getCurrentPlayerLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }

}
