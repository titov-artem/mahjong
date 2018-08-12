package com.github.mahjong.league.controller.impl;

import com.github.mahjong.league.controller.InvitationController;
import com.github.mahjong.league.controller.dto.InvitationForm;
import com.github.mahjong.league.controller.dto.InvitationView;
import com.github.mahjong.league.exceptions.InvitationNotFoundException;
import com.github.mahjong.league.exceptions.PlayerNotFoundException;
import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.repo.InvitationRepo;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.service.InvitationService;
import com.github.mahjong.league.service.PlayerCacheService;
import com.github.mahjong.league.service.model.Player;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller("invitationController")
public class InvitationControllerImpl extends AbstractLeagueAwareController implements InvitationController {

    private final InvitationService invitationService;
    private final InvitationRepo invitationRepo;

    @Inject
    public InvitationControllerImpl(PlayerCacheService playerCacheService,
                                    LeagueRepo leagueRepo,
                                    InvitationService invitationService,
                                    InvitationRepo invitationRepo) {
        super(playerCacheService, leagueRepo);
        this.invitationService = invitationService;
        this.invitationRepo = invitationRepo;
    }

    @Override
    public List<InvitationView> getAll() {
        Player currentPlayer = getCurrentPlayer();
        List<Invitation> invitations = invitationRepo.getAllActive(currentPlayer.getId());
        Set<Long> playerIds = invitations.stream()
                .map(Invitation::getCreatedBy)
                .collect(toSet());
        playerIds.add(currentPlayer.getId());
        Map<Long, Player> players = playerCacheService.getPlayersById(playerIds).stream()
                .collect(toMap(Player::getId, identity()));
        Map<Long, League> leagues = leagueRepo.getAll(invitations.stream().map(Invitation::getLeagueId).collect(toSet())).stream()
                .collect(toMap(League::getId, identity()));
        return invitations.stream()
                .map(invitation -> InvitationView.from(invitation, leagues, players, currentPlayer.getLang()))
                .collect(toList());
    }

    @Override
    public InvitationView create(@NotNull InvitationForm form) {
        Player currentPlayer = getCurrentPlayer();
        // todo maybe here we should load league player instead of all these stuff?
        League league = getLeagueInternal(form.leagueId, false);
        Player player = playerCacheService.getPlayerById(form.playerId)
                .orElseThrow(PlayerNotFoundException.supplier(form.playerId));
        Invitation invitation = invitationService.create(league, player, currentPlayer);
        Map<Long, Player> players = playerCacheService.getPlayersById(ImmutableSet.of(
                currentPlayer.getId(), form.playerId)
        ).stream()
                .collect(toMap(Player::getId, identity()));
        return InvitationView.from(invitation,
                Collections.singletonMap(league.getId(), league),
                players,
                currentPlayer.getLang());
    }

    @Override
    public void accept(@NotNull String code) {
        Player currentPlayer = getCurrentPlayer();
        Invitation invitation = invitationRepo.getActiveByPlayerAndCode(currentPlayer.getId(), code)
                .orElseThrow(InvitationNotFoundException.supplier(code));
        invitationService.accept(invitation);
    }

    @Override
    public void reject(@NotNull String code) {
        Player currentPlayer = getCurrentPlayer();
        Invitation invitation = invitationRepo.getActiveByPlayerAndCode(currentPlayer.getId(), code)
                .orElseThrow(InvitationNotFoundException.supplier(code));
        invitationService.reject(invitation);
    }
}
