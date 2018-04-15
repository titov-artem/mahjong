package com.github.mahjong.main.controller.dto;

import com.github.mahjong.api.common.dto.PlayerShortView;
import com.github.mahjong.main.model.Player;

public class PlayerShortViewHelper {

    public static PlayerShortView from(Player player) {
        PlayerShortView view = new PlayerShortView();
        view.id = player.getId();
        view.name = player.getName();
        return view;
    }

}
