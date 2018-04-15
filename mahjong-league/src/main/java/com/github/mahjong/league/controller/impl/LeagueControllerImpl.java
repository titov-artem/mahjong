package com.github.mahjong.league.controller.impl;

import com.github.mahjong.common.exceptions.BadRequest;
import com.github.mahjong.common.exceptions.PreconditionFailed;
import com.github.mahjong.league.controller.LeagueController;
import com.github.mahjong.league.controller.dto.LeagueForm;
import com.github.mahjong.league.controller.dto.LeagueView;
import com.github.mahjong.league.controller.dto.LeagueView.MemberInfo;
import com.github.mahjong.league.exceptions.PlayerNotFoundException;
import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.model.JoinRequest;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.InvitationRepo;
import com.github.mahjong.league.repo.JoinRequestRepo;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.service.LeagueService;
import com.github.mahjong.league.service.PlayerCacheService;
import com.github.mahjong.league.service.model.Player;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller("leagueController")
public class LeagueControllerImpl extends AbstractLeagueAwareController implements LeagueController {

    private final LeagueService leagueService;
    private final LeaguePlayerRepo leaguePlayerRepo;
    private final JoinRequestRepo joinRequestRepo;
    private final InvitationRepo invitationRepo;

    @Inject
    public LeagueControllerImpl(PlayerCacheService playerCacheService,
                                LeagueRepo leagueRepo,
                                LeagueService leagueService,
                                LeaguePlayerRepo leaguePlayerRepo,
                                JoinRequestRepo joinRequestRepo,
                                InvitationRepo invitationRepo) {
        super(playerCacheService, leagueRepo);
        this.leagueService = leagueService;
        this.leaguePlayerRepo = leaguePlayerRepo;
        this.joinRequestRepo = joinRequestRepo;
        this.invitationRepo = invitationRepo;
    }

    @Override
    public List<LeagueView> getAll() {
        Player currentPlayer = getCurrentPlayer();
        Set<Long> joinedLeagueIds = leaguePlayerRepo.getAllByPlayer(currentPlayer.getId()).stream()
                .map(LeaguePlayer::getLeagueId)
                .collect(toSet());
        Set<Long> requestedLeagueIds = joinRequestRepo.getPendingByPlayerId(currentPlayer.getId()).stream()
                .map(JoinRequest::getLeagueId)
                .collect(toSet());
        Map<Long, Invitation> invitationByLeague = invitationRepo.getAllActive(currentPlayer.getId()).stream()
                .collect(toMap(Invitation::getLeagueId, identity()));
        return leagueRepo.getAll().stream()
                .map(l -> LeagueView.from(
                        l,
                        currentPlayer.getLang(),
                        MemberInfo.of(
                                joinedLeagueIds.contains(l.getId()),
                                requestedLeagueIds.contains(l.getId()),
                                Optional.ofNullable(invitationByLeague.get(l.getId()))
                        )
                ))
                .collect(toList());
    }

    @Override
    public List<LeagueView> getAllJoined() {
        Player currentPlayer = getCurrentPlayer();
        return leagueRepo.getAllWithPlayer(currentPlayer.getId()).stream()
                .map(l -> LeagueView.from(l, currentPlayer.getLang(), MemberInfo.forMember()))
                .collect(toList());
    }

    @Override
    public List<LeagueView> getAllAdmined() {
        Player currentPlayer = getCurrentPlayer();
        return leagueRepo.getAllWithAdmin(currentPlayer.getId()).stream()
                .map(l -> LeagueView.from(l, currentPlayer.getLang(), MemberInfo.forMember()))
                .collect(toList());
    }

    @Override
    public LeagueView create(LeagueForm form) {
        League league = form.toLeague();
        for (Long adminId : league.getAdmins()) {
            //noinspection ResultOfMethodCallIgnored: check that admin players exists
            playerCacheService.getPlayerById(adminId)
                    .orElseThrow(PlayerNotFoundException.supplier(adminId));
        }
        Player currentPlayer = getCurrentPlayer();
        return LeagueView.from(
                leagueService.create(league.withAdmin(currentPlayer.getId())),
                currentPlayer.getLang(),
                MemberInfo.forMember()
        );
    }

    @Override
    public LeagueView get(Long id) {
        Player currentPlayer = getCurrentPlayer();
        Optional<LeaguePlayer> leaguePlayer = leaguePlayerRepo.get(id, currentPlayer.getId());
        Optional<JoinRequest> request = joinRequestRepo.getPendingByPlayerAndLeague(currentPlayer.getId(), id);
        Optional<Invitation> invitation = invitationRepo.getActiveByPlayerAndLeague(currentPlayer.getId(), id);
        return LeagueView.from(
                getLeagueInternal(id, false),
                currentPlayer.getLang(),
                MemberInfo.of(leaguePlayer.isPresent(), request.isPresent(), invitation)
        );
    }

    @Override
    public LeagueView addAdmin(Long id, Set<Long> admins) {
        League league = getLeagueInternal(id, true);
        Set<Long> leaguePlayerIds = leaguePlayerRepo.getAllByLeague(league.getId(), admins).stream()
                .map(LeaguePlayer::getPlayerId)
                .collect(toSet());
        if (!leaguePlayerIds.containsAll(admins)) {
            // todo move code to enum
            throw new BadRequest("league.admin.has.to.belong.to.league");
        }
        for (Long adminId : admins) {
            //noinspection ResultOfMethodCallIgnored: check that admin players exists
            playerCacheService.getPlayerById(adminId)
                    .orElseThrow(PlayerNotFoundException.supplier(adminId));
        }
        leagueRepo.addAdmins(league.getId(), admins);
        return LeagueView.from(
                leagueRepo.get(league.getId()).orElseThrow(noSuchLeague(id)),
                getCurrentPlayer().getLang(),
                MemberInfo.forMember()
        );
    }

    @Override
    public LeagueView removeAdmin(Long id, Set<Long> admins) {
        League league = getLeagueInternal(id, true);
        for (Long adminId : admins) {
            //noinspection ResultOfMethodCallIgnored: check that admin players exists
            playerCacheService.getPlayerById(adminId)
                    .orElseThrow(PlayerNotFoundException.supplier(adminId));
        }
        if (Sets.difference(league.getAdmins(), admins).isEmpty()) {
            // todo move code to enum
            throw new PreconditionFailed("league.cant.remove.all.admin");
        }
        leagueRepo.removeAdmins(league.getId(), admins);
        return LeagueView.from(
                leagueRepo.get(league.getId()).orElseThrow(noSuchLeague(id)),
                getCurrentPlayer().getLang(),
                MemberInfo.forMember()
        );
    }

}
