package com.github.mahjong.main.repo;

import com.github.mahjong.main.model.Game;
import com.github.mahjong.main.model.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GameRepo {

    Game create(Game game);

    Optional<Game> get(Long id);

    List<Game> getAll(Collection<Long> ids);

    List<Game> getAllByPlayer(Player player);

    List<Game> getActiveByPlayer(Player player);

    Game update(Game game);

    void delete(Game game);
}
