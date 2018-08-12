package com.github.mahjong.league.controller.impl;

import com.github.mahjong.league.controller.JoinRequestController;
import com.github.mahjong.league.controller.dto.JoinRequestForm;
import com.github.mahjong.league.controller.dto.JoinRequestRejectForm;
import com.github.mahjong.league.controller.dto.JoinRequestView;
import com.github.mahjong.league.exceptions.JoinRequestNotFoundException;
import com.github.mahjong.league.model.JoinRequest;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.repo.JoinRequestRepo;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.service.JoinRequestService;
import com.github.mahjong.league.service.PlayerCacheService;
import com.github.mahjong.league.service.model.Player;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller("joinRequestController")
public class JoinRequestControllerImpl extends AbstractLeagueAwareController implements JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final JoinRequestRepo joinRequestRepo;

    @Inject
    JoinRequestControllerImpl(PlayerCacheService playerCacheService,
                              LeagueRepo leagueRepo,
                              JoinRequestService joinRequestService,
                              JoinRequestRepo joinRequestRepo) {
        super(playerCacheService, leagueRepo);
        this.joinRequestService = joinRequestService;
        this.joinRequestRepo = joinRequestRepo;
    }

    @Override
    public List<JoinRequestView> getAllIngoing() {
        Player currentPlayer = getCurrentPlayer();
        List<JoinRequest> requests = joinRequestRepo.getByPlayerId(currentPlayer.getId());
        Map<Long, Player> players = getRelatedPlayers(requests);
        return requests.stream()
                .map(req -> JoinRequestView.from(req, players))
                .collect(toList());
    }

    @Override
    public List<JoinRequestView> getAllIngoing(@NotNull Long leagueId) {
        // check that current player is admin of this league
        League league = getLeagueInternal(leagueId, true);
        Player currentPlayer = getCurrentPlayer();
        List<JoinRequest> requests = joinRequestRepo.getPendingForAdmin(league.getId(), currentPlayer.getId());
        Map<Long, Player> players = getRelatedPlayers(requests);
        return requests.stream()
                .map(req -> JoinRequestView.from(req, players))
                .collect(toList());
    }

    @Override
    public JoinRequestView create(@NotNull JoinRequestForm form) {
        // check, that league exists
        League league = getLeagueInternal(form.leagueId, false);
        Player currentPlayer = getCurrentPlayer();
        return JoinRequestView.from(
                joinRequestService.create(league, currentPlayer),
                ImmutableMap.of(currentPlayer.getId(), currentPlayer)
        );
    }

    @Override
    public void approve(@NotNull Long id) {
        JoinRequest request = joinRequestRepo.get(id)
                .orElseThrow(JoinRequestNotFoundException.supplier(id));
        League league = getLeagueInternal(request.getLeagueId(), true);
        joinRequestService.approve(league, request, getCurrentPlayer());
    }

    @Override
    public void reject(@NotNull Long id, @NotNull JoinRequestRejectForm form) {
        Preconditions.checkArgument(Objects.equals(id, form.id), "Path id and form id are diffferent");
        JoinRequest request = joinRequestRepo.get(id)
                .orElseThrow(JoinRequestNotFoundException.supplier(id));
        League league = getLeagueInternal(request.getLeagueId(), true);
        joinRequestService.reject(league, request, getCurrentPlayer(), form.reason);
    }

    private Map<Long, Player> getRelatedPlayers(Collection<JoinRequest> requests) {
        Set<Long> playerIds = new HashSet<>();
        requests.forEach(req -> {
            playerIds.add(req.getPlayerId());
            if (req.getReviewedBy() != null) {
                playerIds.add(req.getReviewedBy());
            }
        });
        return playerCacheService.getPlayersById(playerIds).stream()
                .collect(toMap(Player::getId, identity()));
    }
}
