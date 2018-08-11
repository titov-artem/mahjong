package com.github.mahjong.league.service

import com.github.mahjong.common.enums.LangIso639
import com.github.mahjong.common.jdbc.DummyTxHelper
import com.github.mahjong.common.test.DummyClock
import com.github.mahjong.league.model.JoinRequest
import com.github.mahjong.league.model.League
import com.github.mahjong.league.model.LeaguePlayer
import com.github.mahjong.league.repo.JoinRequestRepo
import com.github.mahjong.league.repo.LeaguePlayerRepo
import com.github.mahjong.league.service.model.Player
import spock.lang.Specification

import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

class JoinRequestServiceTest extends Specification {

    private final DummyClock clock = new DummyClock()

    def "approve; by admin"() {
        given:
        def admin = new Player(2L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)

        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, request.playerId) >> { Long leagueId, Long playerId ->
                return Optional.empty()
            }
        }
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())
        def updatedRequest;

        when:
        service.approve(league, request, admin)

        then:
        noExceptionThrown()
        1 * jrRepo.update(_) >> { JoinRequest r -> updatedRequest = r }
        updatedRequest.decision == JoinRequest.Decision.APPROVED
        updatedRequest.reviewedBy == admin.id
        1 * lpRepo.create(new LeaguePlayer(request.leagueId, request.playerId))
    }

    def "approve; by not admin"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())

        def admin = new Player(1L, "", "", LangIso639.EN)
        def player = new Player(2L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)

        when:
        service.approve(league, request, player)

        then:
        thrown(IllegalArgumentException)
    }

    def "approve; by admin; wrong league"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())

        def admin = new Player(1L, "", "", LangIso639.EN)
        def league1 = new League(1L, [:], [:], [admin.id] as Set)
        def league2 = new League(2L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league1.id)

        when:
        service.approve(league2, request, admin)

        then:
        thrown(IllegalArgumentException)
    }

    def "approve; by admin; expired"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(
                jrRepo,
                lpRepo,
                Clock.offset(clock, Duration.ofDays(JoinRequestService.JOIN_REQUEST_EXPIRE_PERIOD_DAYS + 1)),
                new DummyTxHelper())

        def admin = new Player(1L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)

        when:
        service.approve(league, request, admin)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject; by admin"() {
        given:
        def admin = new Player(2L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)
        def reason = "reason"

        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo) {
            get(league.id, request.playerId) >> { Long leagueId, Long playerId ->
                return Optional.empty()
            }
        }
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())
        def updatedRequest;

        when:
        service.reject(league, request, admin, reason)

        then:
        noExceptionThrown()
        1 * jrRepo.update(_) >> { JoinRequest r -> updatedRequest = r }
        updatedRequest.decision == JoinRequest.Decision.REJECTED
        updatedRequest.reviewedBy == admin.id
        0 * lpRepo.create(_ as LeaguePlayer)
    }

    def "reject; by not admin"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())
        def reason = "reason"

        def admin = new Player(1L, "", "", LangIso639.EN)
        def player = new Player(2L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)

        when:
        service.reject(league, request, player, reason)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject; by admin; wrong league"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(jrRepo, lpRepo, clock, new DummyTxHelper())
        def reason = "reason"

        def admin = new Player(1L, "", "", LangIso639.EN)
        def league1 = new League(1L, [:], [:], [admin.id] as Set)
        def league2 = new League(2L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league1.id)

        when:
        service.reject(league2, request, admin, reason)

        then:
        thrown(IllegalArgumentException)
    }

    def "reject; by admin; expired"() {
        given:
        def jrRepo = Mock(JoinRequestRepo)
        def lpRepo = Mock(LeaguePlayerRepo)
        def service = new JoinRequestService(
                jrRepo,
                lpRepo,
                Clock.offset(clock, Duration.ofDays(JoinRequestService.JOIN_REQUEST_EXPIRE_PERIOD_DAYS + 1)),
                new DummyTxHelper())
        def reason = "reason"

        def admin = new Player(1L, "", "", LangIso639.EN)
        def league = new League(1L, [:], [:], [admin.id] as Set)
        def request = pendingRequest(3L, league.id)

        when:
        service.reject(league, request, admin, reason)

        then:
        thrown(IllegalArgumentException)
    }

    def pendingRequest(long playerId, long leagueId) {
        return new JoinRequest(
                1L,
                leagueId,
                playerId,
                LocalDateTime.now(clock),
                JoinRequest.Decision.PENDING,
                "",
                null,
                null,
                LocalDateTime.now(clock).plusDays(JoinRequestService.JOIN_REQUEST_EXPIRE_PERIOD_DAYS)
        )
    }
}
