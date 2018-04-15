package com.github.mahjong.league.service;

import com.github.mahjong.common.enums.EnumUtils;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.league.service.model.Player;
import com.github.mahjong.main.privateapi.PlayerController;
import com.github.mahjong.main.privateapi.dto.PlayerDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Service
public class PlayerCacheService {

    private final PlayerController playerController;
    private final LoadingCache<String, Player> playerByLoginCache;

    @Inject
    public PlayerCacheService(PlayerController playerController) {
        this.playerController = playerController;
        playerByLoginCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Player>() {
                    @Override
                    public Player load(@Nonnull String key) throws Exception {
                        PlayerDto dto = playerController.getByLogin(key);
                        return toPlayer(dto);
                    }
                });
    }

    public Optional<Player> getPlayerByLogin(String login) {
        try {
            return Optional.of(playerByLoginCache.get(login));
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (e.getCause() instanceof NotFoundException) {
                // No player with such login
                return Optional.empty();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<Player> getPlayerById(Long id) {
        for (Player p : playerByLoginCache.asMap().values()) {
            if (Objects.equals(p.getId(), id)) {
                return Optional.of(p);
            }
        }
        try {
            Player player = toPlayer(playerController.get(id));
            playerByLoginCache.put(player.getLogin(), player);
            return Optional.of(player);
        } catch (NotFoundException ignore) {
            return Optional.empty();
        }
    }

    public List<Player> getPlayersById(Set<Long> playerIds) {
        Set<Long> toProcess = new HashSet<>(playerIds);
        List<Player> out = new ArrayList<>();
        for (Player p : playerByLoginCache.asMap().values()) {
            if (toProcess.contains(p.getId())) {
                out.add(p);
                toProcess.remove(p.getId());
            }
        }
        if (!toProcess.isEmpty()) {
            // if some one missing in the cache - load all and update cache
            out = playerController.getAll(playerIds).stream()
                    .map(PlayerCacheService::toPlayer)
                    .collect(toList());
            out.forEach(player -> playerByLoginCache.put(player.getLogin(), player));
        }
        return out;
    }

    private static Player toPlayer(PlayerDto dto) {
        return new Player(dto.id,
                dto.login,
                dto.name,
                EnumUtils.transferClass(dto.lang, LangIso639.class)
        );
    }
}
