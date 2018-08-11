package com.github.mahjong.league.service;

import com.github.mahjong.league.model.JoinRequest;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.JoinRequestRepo;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.common.jdbc.TransactionalHelper;
import com.github.mahjong.league.service.model.Player;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class JoinRequestService {

    public static final int JOIN_REQUEST_EXPIRE_PERIOD_DAYS = 30;

    private final JoinRequestRepo joinRequestRepo;
    private final LeaguePlayerRepo leaguePlayerRepo;

    private final Clock clock;
    private final TransactionalHelper txHelper;

    @Inject
    public JoinRequestService(JoinRequestRepo joinRequestRepo,
                              LeaguePlayerRepo leaguePlayerRepo,
                              Clock clock,
                              TransactionalHelper txHelper) {
        this.joinRequestRepo = joinRequestRepo;
        this.leaguePlayerRepo = leaguePlayerRepo;
        this.clock = clock;
        this.txHelper = txHelper;
    }

    public JoinRequest create(League league, Player player) {
        LocalDateTime now = LocalDateTime.now(clock);
        JoinRequest request = JoinRequest.createNew(league.getId(),
                player.getId(),
                now,
                now.plusDays(JOIN_REQUEST_EXPIRE_PERIOD_DAYS)
        );
        return joinRequestRepo.create(request);
    }

    public void approve(League league, JoinRequest request, Player admin) {
        Preconditions.checkArgument(league.getAdmins().contains(admin.getId()), "Player is not admin");
        Preconditions.checkArgument(Objects.equals(league.getId(), request.getLeagueId()), "Request to wrong league");
        Preconditions.checkArgument(!request.isExpired(clock), "Request expired");
        txHelper.defaultTx(() -> {
            Optional<LeaguePlayer> leaguePlayerOpt = leaguePlayerRepo.get(request.getLeagueId(), request.getPlayerId());
            joinRequestRepo.update(request.approved(admin.getId(), clock));
            if (leaguePlayerOpt.isPresent()) {
                return;
            }
            leaguePlayerRepo.create(new LeaguePlayer(request.getLeagueId(), request.getPlayerId()));
        });
    }

    public void reject(League league, JoinRequest request, Player admin, String reason) {
        Preconditions.checkArgument(league.getAdmins().contains(admin.getId()), "Player is not admin");
        Preconditions.checkArgument(Objects.equals(league.getId(), request.getLeagueId()), "Request to wrong league");
        Preconditions.checkArgument(!request.isExpired(clock), "Request expired");
        joinRequestRepo.update(request.rejected(admin.getId(), reason == null ? "" : reason, clock));
    }
}
