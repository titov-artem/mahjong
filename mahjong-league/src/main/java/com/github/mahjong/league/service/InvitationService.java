package com.github.mahjong.league.service;

import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.InvitationRepo;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.league.repo.TransactionalHelper;
import com.github.mahjong.league.service.model.Player;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    public static final int INVITATION_EXPIRE_PERIOD = 30;

    private final InvitationRepo invitationRepo;
    private final LeaguePlayerRepo leaguePlayerRepo;

    private final Clock clock;
    private final TransactionalHelper txHelper;

    @Inject
    public InvitationService(InvitationRepo invitationRepo,
                             LeaguePlayerRepo leaguePlayerRepo,
                             Clock clock,
                             TransactionalHelper txHelper) {
        this.invitationRepo = invitationRepo;
        this.leaguePlayerRepo = leaguePlayerRepo;
        this.clock = clock;
        this.txHelper = txHelper;
    }

    public Invitation create(League league, Player player, Player author) {
        Optional<LeaguePlayer> leaguePlayerOpt = leaguePlayerRepo.get(league.getId(), author.getId());
        if (!leaguePlayerOpt.isPresent()) {
            throw new IllegalArgumentException("Player " + author.getId() + " doesn't belong to league " + league.getId());
        }
        return txHelper.defaultTx(() -> {
            LocalDateTime now = LocalDateTime.now(clock);
            LocalDateTime expireAt = now.plusDays(INVITATION_EXPIRE_PERIOD);

            Optional<Invitation> activeOpt = invitationRepo.getActiveByPlayerAndLeagueForUpdate(
                    player.getId(), league.getId());
            if (activeOpt.isPresent()) {
                invitationRepo.prolongActive(activeOpt.get().getId(), author.getId(), expireAt);
                //noinspection ConstantConditions: exist becuase found previously and blocked by current transaction
                return invitationRepo.get(activeOpt.get().getId()).get();
            }
            String code = UUID.randomUUID().toString();
            Invitation invitation = Invitation.createNew(league.getId(),
                    player.getId(),
                    code,
                    author.getId(),
                    now,
                    expireAt
            );
            return invitationRepo.create(invitation);
        });
    }

    public void accept(Invitation invitation) {
        Preconditions.checkArgument(!invitation.isExpired(clock), "Invitation expired");
        Preconditions.checkArgument(invitation.isActive(), "Invitation already used");
        txHelper.defaultTx(() -> {
            Optional<LeaguePlayer> leaguePlayerOpt = leaguePlayerRepo.get(invitation.getLeagueId(), invitation.getPlayerId());
            invitationRepo.changeStatus(invitation.getId(), Invitation.Status.ACCEPTED);
            if (leaguePlayerOpt.isPresent()) {
                return;
            }
            leaguePlayerRepo.create(new LeaguePlayer(invitation.getLeagueId(), invitation.getPlayerId()));
        });
    }

    public void reject(Invitation invitation) {
        Preconditions.checkArgument(!invitation.isExpired(clock), "Invitation expired");
        Preconditions.checkArgument(invitation.isActive(), "Invitation already used");
        invitationRepo.changeStatus(invitation.getId(), Invitation.Status.REJECTED);
    }
}
