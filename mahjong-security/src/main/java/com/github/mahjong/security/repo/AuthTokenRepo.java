package com.github.mahjong.security.repo;

import com.github.mahjong.security.model.AuthToken;

import java.util.Optional;

public interface AuthTokenRepo {
    AuthToken create(AuthToken token);

    Optional<AuthToken> get(String login, String token);

    Optional<AuthToken> getNewest(String login);
}
