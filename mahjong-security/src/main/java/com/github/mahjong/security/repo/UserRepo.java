package com.github.mahjong.security.repo;

import com.github.mahjong.security.model.MahjongUser;

import java.util.Optional;

public interface UserRepo {

    MahjongUser create(MahjongUser user);

    Optional<MahjongUser> get(String login);

}
