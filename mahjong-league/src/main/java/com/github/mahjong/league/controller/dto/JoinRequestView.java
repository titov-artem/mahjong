package com.github.mahjong.league.controller.dto;

import com.github.mahjong.api.common.dto.PlayerShortView;
import com.github.mahjong.common.enums.EnumUtils;
import com.github.mahjong.league.model.JoinRequest;
import com.github.mahjong.league.service.model.Player;

import java.time.LocalDateTime;
import java.util.Map;

public class JoinRequestView {

    public long id;
    public long leagueId;
    public PlayerShortView player;
    public DecisionDto decision;
    public String reason;
    public PlayerShortView admin;
    public LocalDateTime consideredAt;
    public LocalDateTime expireAt;

    public enum DecisionDto {
        PENDING, APPROVED, REJECTED
    }

    public static JoinRequestView from(JoinRequest request, Map<Long, Player> players) {
        JoinRequestView view = new JoinRequestView();
        view.id = request.getId();
        view.leagueId = request.getLeagueId();
        view.player = PlayerShortViewHelper.from(players.get(request.getPlayerId()));
        view.decision = EnumUtils.transferClass(request.getDecision(), DecisionDto.class);
        if (request.getReason() != null) {
            view.reason = request.getReason();
        }
        if (request.getReviewedBy() != null) {
            view.admin = PlayerShortViewHelper.from(players.get(request.getReviewedBy()));
        }
        if (request.getReviewedAt() != null) {
            view.consideredAt = request.getReviewedAt();
        }
        view.expireAt = request.getExpireAt();
        return view;
    }

}
