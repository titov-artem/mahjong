package com.github.mahjong.main.controller.privateapi.dto;

import com.github.mahjong.api.common.LangIso639Dto;
import com.github.mahjong.common.enums.EnumUtils;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.privateapi.dto.PlayerDto;
import com.google.common.base.Preconditions;

public class PlayerDtoHelper {

    public static PlayerDto from(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.id = player.getId();
        dto.login = player.getLogin();
        dto.name = player.getName();
        dto.lang = EnumUtils.transferClass(player.getLang(), LangIso639Dto.class);
        return dto;
    }

    public static Player toPlayer(PlayerDto dto) {
        Preconditions.checkNotNull(dto.id, "Id not specified");
        return new Player(dto.id,
                dto.login,
                dto.name,
                EnumUtils.transferClass(dto.lang, LangIso639.class)
        );
    }

}
