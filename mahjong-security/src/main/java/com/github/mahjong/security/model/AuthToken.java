package com.github.mahjong.security.model;

import lombok.Data;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
public class AuthToken {

    private final String login;
    private final String token;
    private final LocalDateTime expireAt;

    public boolean isExpired(Clock clock) {
        return expireAt.isBefore(LocalDateTime.now(clock));
    }
}
