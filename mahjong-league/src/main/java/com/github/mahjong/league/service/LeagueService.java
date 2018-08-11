package com.github.mahjong.league.service;

import com.github.mahjong.league.model.League;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.common.jdbc.TransactionalHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class LeagueService {

    private final LeagueRepo leagueRepo;
    private final LeaguePlayerRepo leaguePlayerRepo;

    private final TransactionalHelper txHelper;

    @Inject
    public LeagueService(LeagueRepo leagueRepo,
                         LeaguePlayerRepo leaguePlayerRepo,
                         TransactionalHelper txHelper) {
        this.leagueRepo = leagueRepo;
        this.leaguePlayerRepo = leaguePlayerRepo;
        this.txHelper = txHelper;
    }

    public League create(League league) {
        return txHelper.defaultTx(() -> {
            League created = leagueRepo.create(league);
            for (Long adminId : created.getAdmins()) {
                leaguePlayerRepo.create(new LeaguePlayer(created.getId(), adminId));
            }
            return created;
        });
    }
}
