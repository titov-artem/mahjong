package com.github.mahjong.league.controller.impl;

import com.github.mahjong.league.service.PlayerCacheService;
import com.github.mahjong.league.service.model.Player;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class AbstractUserController {

    final PlayerCacheService playerCacheService;

    AbstractUserController(PlayerCacheService playerCacheService) {
        this.playerCacheService = playerCacheService;
    }

    final Player getCurrentPlayer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //noinspection ConstantConditions: Player was authenticated so it has to exist
        return playerCacheService.getPlayerByLogin(((UserDetails) auth.getPrincipal()).getUsername()).get();
    }
}
