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

import java.time.Clock
import java.time.LocalDateTime

import static com.github.mahjong.common.enums.LangIso639.EN
import static java.time.Duration.ofDays

class InvitationServiceTest extends Specification {
    private final DummyClock clock = new DummyClock()
    private final DummyTxHelper txHelper = new DummyTxHelper()
    private final int expPeriod = InvitationService.INVITATION_EXPIRE_PERIOD_DAYS

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
        invitation.expireAt == LocalDateTime.now(clock).plusDays(expPeriod)
    }

    def "create; prolong existing"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def oldAuthor = new Player(4L, "old_author", "old author", EN)
        def existingInvitation = new Invitation(
                1L, league.id, player.id, "code", oldAuthor.id,
                LocalDateTime.now(clock).minusDays(10), LocalDateTime.now(clock).plusDays(expPeriod - 10),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo) {
            get(existingInvitation.id) >> Optional.of(new Invitation(
                    existingInvitation.id, existingInvitation.leagueId, existingInvitation.playerId,
                    existingInvitation.code, existingInvitation.createdBy,
                    existingInvitation.createdAt, LocalDateTime.now(clock).plusDays(expPeriod),
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
        invitation.expireAt == LocalDateTime.now(clock).plusDays(expPeriod)
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

    def "accept"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.empty()
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.accept(invitation)

        then:
        1 * iRepo.changeStatus(invitation.id, Invitation.Status.ACCEPTED)
        1 * lpRepo.create(new LeaguePlayer(league.id, player.id))
    }

    def "accept; already in league"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.accept(invitation)

        then:
        1 * iRepo.changeStatus(invitation.id, Invitation.Status.ACCEPTED)
        0 * lpRepo.create(_)
    }

    def "accept; already expired"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, Clock.offset(clock, ofDays(expPeriod + 1)), txHelper)

        when:
        service.accept(invitation)

        then:
        thrown(IllegalArgumentException)
    }

    def "accept; already accepted"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACCEPTED
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.accept(invitation)

        then:
        thrown(IllegalArgumentException)
    }

    def "accept; already rejected"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.REJECTED
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.accept(invitation)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.empty()
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.reject(invitation)

        then:
        1 * iRepo.changeStatus(invitation.id, Invitation.Status.REJECTED)
        0 * lpRepo.create(_)
    }

    def "reject; already expired"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACTIVE
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, Clock.offset(clock, ofDays(expPeriod + 1)), txHelper)

        when:
        service.reject(invitation)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject; already accepted"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.ACCEPTED
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.reject(invitation)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject; already rejected"() {
        given:
        def league = new League(1L, [:], [:], [] as Set)
        def player = new Player(2L, "player", "player", EN)
        def author = new Player(3L, "author", "author", EN)
        def invitation = new Invitation(
                1L, league.id, player.id, "code", author.id,
                LocalDateTime.now(clock), LocalDateTime.now(clock).plusDays(expPeriod),
                Invitation.Status.REJECTED
        )

        def iRepo = Mock(InvitationRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, player.id) >> Optional.of(new LeaguePlayer(league.id, player.id))
        }
        def service = new InvitationService(iRepo, lpRepo, clock, txHelper)

        when:
        service.reject(invitation)

        then:
        thrown(IllegalArgumentException)
    }

}
