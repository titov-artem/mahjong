package com.github.mahjong.league.service


import com.github.mahjong.common.jdbc.DummyTxHelper
import com.github.mahjong.common.test.DummyClock
import com.github.mahjong.league.model.Invitation
import com.github.mahjong.league.model.League
import com.github.mahjong.league.model.LeaguePlayer
import com.github.mahjong.league.repo.InvitationRepo
import com.github.mahjong.league.repo.LeaguePlayerRepo
import com.github.mahjong.league.service.model.Player
import spock.lang.Specification

import java.time.LocalDateTime

import static com.github.mahjong.common.enums.LangIso639.EN

class InvitationServiceTest extends Specification {
    private final DummyClock clock = new DummyClock()
    private final DummyTxHelper txHelper = new DummyTxHelper()

    def "create"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)

        def iRepo = Mock(InvitationRepo) {
            create(_) >> { Invitation i -> return i }
            getActiveByPlayerAndLeagueForUpdate(player.id, league.id) >> Optional.empty()
        }
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, author.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        def invitation = service.create(league, player, author)

        then:
        invitation.leagueId == league.id
        invitation.playerId == player.id
        invitation.status == Invitation.Status.ACTIVE
        invitation.expireAt == LocalDateTime.now(clock).plusDays(InvitationService.INVITATION_EXPIRE_PERIOD_DAYS)
    }

    def "create; prolong existing"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def oldAuthor = new Player(4L, "old_author", "old author", EN)
        def existingInvitation = new Invitation(
                1L, league.id, player.id, "code", oldAuthor.id,
                LocalDateTime.now(clock).minusDays(10), LocalDateTime.now(clock).plusDays(InvitationService.INVITATION_EXPIRE_PERIOD_DAYS - 10),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo) {
            get(existingInvitation.id) >> Optional.of(new Invitation(
                    existingInvitation.id, existingInvitation.leagueId, existingInvitation.playerId,
                    existingInvitation.code, existingInvitation.createdBy,
                    existingInvitation.createdAt, LocalDateTime.now(clock).plusDays(InvitationService.INVITATION_EXPIRE_PERIOD_DAYS),
                    Invitation.Status.ACTIVE
            ))
            getActiveByPlayerAndLeagueForUpdate(player.id, league.id) >> Optional.of(existingInvitation)
        }
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, author.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        def invitation = service.create(league, player, author)

        then:
        0 * iRepo.create(_)
        invitation.leagueId == league.id
        invitation.playerId == player.id
        invitation.status == Invitation.Status.ACTIVE
        invitation.expireAt == LocalDateTime.now(clock).plusDays(InvitationService.INVITATION_EXPIRE_PERIOD_DAYS)
    }

    def "create; author not from league"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, author.id) >> Optional.empty()
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.create(league, player, author)

        then:
        thrown(IllegalArgumentException)
        0 * iRepo.create(_)
        0 * iRepo.prolongActive(_ as Long, _ as Long, _ as LocalDateTime)
    }
}
