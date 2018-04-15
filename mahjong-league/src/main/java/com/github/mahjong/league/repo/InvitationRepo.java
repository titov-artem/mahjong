package com.github.mahjong.league.repo;

import com.github.mahjong.league.model.Invitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvitationRepo {

    Optional<Invitation> get(Long id);

    List<Invitation> getAllActive(Long playerId);

    Optional<Invitation> getActiveByPlayerAndLeague(Long playerId, Long leagueId);

    Optional<Invitation> getActiveByPlayerAndLeagueForUpdate(Long playerId, Long leagueId);

    Optional<Invitation> getActiveByPlayerAndCode(Long playerId, String code);

    Invitation create(Invitation invitation);

    void prolongActive(Long id, Long authorId, LocalDateTime expireAt);

    void changeStatus(Long id, Invitation.Status status);
}
