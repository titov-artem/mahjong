/*
 * This file is generated by jOOQ.
*/
package com.github.mahjong.league.repo.jdbc.generated.tables.records;


import com.github.mahjong.league.repo.jdbc.generated.tables.LeagueGame;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LeagueGameRecord extends UpdatableRecordImpl<LeagueGameRecord> implements Record3<Long, Long, Long[]> {

    private static final long serialVersionUID = 1936361679;

    /**
     * Setter for <code>public.league_game.league_id</code>.
     */
    public void setLeagueId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.league_game.league_id</code>.
     */
    public Long getLeagueId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.league_game.game_id</code>.
     */
    public void setGameId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.league_game.game_id</code>.
     */
    public Long getGameId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.league_game.player_ids</code>.
     */
    public void setPlayerIds(Long... value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.league_game.player_ids</code>.
     */
    public Long[] getPlayerIds() {
        return (Long[]) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Long, Long, Long[]> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Long, Long, Long[]> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return LeagueGame.LEAGUE_GAME.LEAGUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return LeagueGame.LEAGUE_GAME.GAME_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long[]> field3() {
        return LeagueGame.LEAGUE_GAME.PLAYER_IDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getLeagueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component2() {
        return getGameId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long[] component3() {
        return getPlayerIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getLeagueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getGameId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long[] value3() {
        return getPlayerIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeagueGameRecord value1(Long value) {
        setLeagueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeagueGameRecord value2(Long value) {
        setGameId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeagueGameRecord value3(Long... value) {
        setPlayerIds(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeagueGameRecord values(Long value1, Long value2, Long[] value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LeagueGameRecord
     */
    public LeagueGameRecord() {
        super(LeagueGame.LEAGUE_GAME);
    }

    /**
     * Create a detached, initialised LeagueGameRecord
     */
    public LeagueGameRecord(Long leagueId, Long gameId, Long[] playerIds) {
        super(LeagueGame.LEAGUE_GAME);

        set(0, leagueId);
        set(1, gameId);
        set(2, playerIds);
    }
}
