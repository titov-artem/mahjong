package com.github.mahjong.league.repo;

import com.github.mahjong.league.model.JoinRequest;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepo {

    Optional<JoinRequest> get(Long id);

    List<JoinRequest> getByPlayerId(Long playerId);

    List<JoinRequest> getPendingByPlayerId(Long playerId);

    Optional<JoinRequest> getPendingByPlayerAndLeague(Long playerId, Long leagueId);

    List<JoinRequest> getPendingForAdmin(Long leagueId, Long adminId);

    JoinRequest create(JoinRequest request);

    JoinRequest update(JoinRequest request);
}
