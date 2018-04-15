package com.github.mahjong.security.model;

import lombok.Data;

@Data
public class MahjongUser {

    private final String login;
    /**
     * Stored encoded password
     */
    private final String password;

}
