package com.github.mahjong.security.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CookieData {
    private String login;
    private String token;
    private LocalDateTime expireAt;
}
