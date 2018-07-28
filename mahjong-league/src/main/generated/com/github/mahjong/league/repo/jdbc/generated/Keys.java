/*
 * This file is generated by jOOQ.
*/
package com.github.mahjong.league.repo.jdbc.generated;


import com.github.mahjong.league.repo.jdbc.generated.tables.Invitation;
import com.github.mahjong.league.repo.jdbc.generated.tables.JoinRequest;
import com.github.mahjong.league.repo.jdbc.generated.tables.League;
import com.github.mahjong.league.repo.jdbc.generated.tables.LeagueGame;
import com.github.mahjong.league.repo.jdbc.generated.tables.LeaguePlayer;
import com.github.mahjong.league.repo.jdbc.generated.tables.SchemaVersion;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.InvitationRecord;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.JoinRequestRecord;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.LeagueGameRecord;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.LeaguePlayerRecord;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.LeagueRecord;
import com.github.mahjong.league.repo.jdbc.generated.tables.records.SchemaVersionRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<InvitationRecord, Long> IDENTITY_INVITATION = Identities0.IDENTITY_INVITATION;
    public static final Identity<JoinRequestRecord, Long> IDENTITY_JOIN_REQUEST = Identities0.IDENTITY_JOIN_REQUEST;
    public static final Identity<LeagueRecord, Long> IDENTITY_LEAGUE = Identities0.IDENTITY_LEAGUE;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<InvitationRecord> INVITATION_PKEY = UniqueKeys0.INVITATION_PKEY;
    public static final UniqueKey<JoinRequestRecord> JOIN_REQUEST_PKEY = UniqueKeys0.JOIN_REQUEST_PKEY;
    public static final UniqueKey<LeagueRecord> LEAGUE_PKEY = UniqueKeys0.LEAGUE_PKEY;
    public static final UniqueKey<LeagueGameRecord> LEAGUE_GAME_PKEY = UniqueKeys0.LEAGUE_GAME_PKEY;
    public static final UniqueKey<LeaguePlayerRecord> LEAGUE_PLAYER_PKEY = UniqueKeys0.LEAGUE_PLAYER_PKEY;
    public static final UniqueKey<SchemaVersionRecord> SCHEMA_VERSION_PK = UniqueKeys0.SCHEMA_VERSION_PK;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<InvitationRecord, Long> IDENTITY_INVITATION = Internal.createIdentity(Invitation.INVITATION, Invitation.INVITATION.ID);
        public static Identity<JoinRequestRecord, Long> IDENTITY_JOIN_REQUEST = Internal.createIdentity(JoinRequest.JOIN_REQUEST, JoinRequest.JOIN_REQUEST.ID);
        public static Identity<LeagueRecord, Long> IDENTITY_LEAGUE = Internal.createIdentity(League.LEAGUE, League.LEAGUE.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<InvitationRecord> INVITATION_PKEY = Internal.createUniqueKey(Invitation.INVITATION, "invitation_pkey", Invitation.INVITATION.ID);
        public static final UniqueKey<JoinRequestRecord> JOIN_REQUEST_PKEY = Internal.createUniqueKey(JoinRequest.JOIN_REQUEST, "join_request_pkey", JoinRequest.JOIN_REQUEST.ID);
        public static final UniqueKey<LeagueRecord> LEAGUE_PKEY = Internal.createUniqueKey(League.LEAGUE, "league_pkey", League.LEAGUE.ID);
        public static final UniqueKey<LeagueGameRecord> LEAGUE_GAME_PKEY = Internal.createUniqueKey(LeagueGame.LEAGUE_GAME, "league_game_pkey", LeagueGame.LEAGUE_GAME.LEAGUE_ID, LeagueGame.LEAGUE_GAME.GAME_ID);
        public static final UniqueKey<LeaguePlayerRecord> LEAGUE_PLAYER_PKEY = Internal.createUniqueKey(LeaguePlayer.LEAGUE_PLAYER, "league_player_pkey", LeaguePlayer.LEAGUE_PLAYER.LEAGUE_ID, LeaguePlayer.LEAGUE_PLAYER.PLAYER_ID);
        public static final UniqueKey<SchemaVersionRecord> SCHEMA_VERSION_PK = Internal.createUniqueKey(SchemaVersion.SCHEMA_VERSION, "schema_version_pk", SchemaVersion.SCHEMA_VERSION.INSTALLED_RANK);
    }
}