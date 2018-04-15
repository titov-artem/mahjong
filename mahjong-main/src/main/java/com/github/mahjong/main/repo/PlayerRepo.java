package com.github.mahjong.main.repo;

import com.github.mahjong.main.model.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlayerRepo {

    Player create(Player player);

    Optional<Player> get(Long id);

    Optional<Player> getByLogin(String login);

    List<Player> getAll();

    List<Player> getAll(Collection<Long> ids);

    Optional<Player> update(Player player);

    void delete(Player player);

}
