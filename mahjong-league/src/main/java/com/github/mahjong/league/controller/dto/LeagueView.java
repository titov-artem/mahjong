package com.github.mahjong.league.controller.dto;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.model.League;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class LeagueView {

    public Long id;
    public String name;
    public String description;
    public Set<Long> admins;
    public MemberInfo memberInfo;

    public static LeagueView from(League league, LangIso639 lang, MemberInfo memberInfo) {
        LeagueView view = new LeagueView();
        view.id = league.getId();
        view.name = lang.getTranslated(league.getName(), LangIso639.EN);
        view.description = lang.getTranslated(league.getDescription(), LangIso639.EN);
        view.admins = league.getAdmins();
        view.memberInfo = memberInfo;
        return view;
    }

    public static class MemberInfo {

        public boolean isJoined;
        public boolean isJoinRequested;
        public boolean isInvited;
        @Nullable
        public String invitationCode;

        public static MemberInfo of(boolean isJoined, boolean isJoinRequested, Optional<Invitation> invitation) {
            return of(isJoined,
                    isJoinRequested,
                    invitation.isPresent(),
                    invitation.map(Invitation::getCode).orElse(null)
            );
        }

        public static MemberInfo of(boolean isJoined, boolean isJoinRequested, boolean isInvited, String invitaionCode) {
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.isJoined = isJoined;
            memberInfo.isJoinRequested = isJoinRequested;
            memberInfo.isInvited = isInvited;
            memberInfo.invitationCode = invitaionCode;
            return memberInfo;
        }

        public static MemberInfo forMember() {
            return of(true, false, false, null);
        }
    }

}
