package com.github.mahjong.league.controller.dto;

import com.github.mahjong.api.common.dto.PlayerShortView;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.service.model.Player;

import java.time.LocalDateTime;
import java.util.Map;

public class InvitationView {

    public long leagueId;
    public String leagueName;
    public PlayerShortView player;
    public String code;
    public PlayerShortView author;
    public LocalDateTime createdAt;
    public LocalDateTime expiredAt;

    public static InvitationView from(Invitation invitation,
                                      Map<Long, League> leagues,
                                      Map<Long, Player> players,
                                      LangIso639 lang) {
        InvitationView view = new InvitationView();
        view.leagueId = invitation.getLeagueId();
        view.leagueName = leagues.get(invitation.getLeagueId()).getName().get(lang);
        view.player = PlayerShortViewHelper.from(players.get(invitation.getPlayerId()));
        view.code = invitation.getCode();
        view.author = PlayerShortViewHelper.from(players.get(invitation.getCreatedBy()));
        view.createdAt = invitation.getCreatedAt();
        view.expiredAt = invitation.getExpireAt();
        return view;
    }

}
