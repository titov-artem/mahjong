import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {LeagueView} from '../../../model/league/league-view';
import {LeagueService} from '../../../services/league.service';
import {LeagueJoinRequestService} from '../../../services/league-join-request.service';
import {JoinRequestForm} from '../../../model/league/join-request-form';
import {LeagueInvitationService} from '../../../services/league-invitation.service';
import {PlayerShort} from '../../../model/player.short';
import {PlayerService} from '../../../services/player.service';

const MODE_JOINED = 1;
const MODE_ALL = 2;
const MODE_ADMINED = 3;

@Component({
    selector: 'league-list',
    templateUrl: './league-list.component.html',
    styleUrls: ['./league-list.component.css']
})
export class LeagueListComponent implements OnInit {

    // we store separately to protect agains concurrent update from different observables
    leagues: LeagueView[];
    joinedLeagues: LeagueView[];
    adminedLeagues: LeagueView[];
    allPlayers: PlayerShort[] = [];

    constructor(private router: Router,
                private leagueService: LeagueService,
                private playerService: PlayerService,
                private joinRequestService: LeagueJoinRequestService,
                private invitationService: LeagueInvitationService) {
    }

    ngOnInit() {
        this.getLeagues(MODE_JOINED);
        this.getLeagues(MODE_ALL);
        this.getLeagues(MODE_ADMINED);
        this.getAllPlayers();
    }

    private getLeagues(mode: number) {
        switch (mode) {
            case MODE_JOINED:
                this.leagueService.getJoinedLeagues().subscribe(leagues => this.joinedLeagues = leagues);
                break;
            case MODE_ALL:
                this.leagueService.getLeagues().subscribe(leagues => this.leagues = leagues);
                break;
            case MODE_ADMINED:
                this.leagueService.getAdminedLeagues().subscribe(leagues => this.adminedLeagues = leagues);
                break;
        }
    }

    private getAllPlayers() {
        this.playerService.getPlayers()
            .subscribe(players => this.allPlayers = players);
    }


    openCreateForm() {
        this.router.navigate(['/leagues/create'])
    }

    joinLeague(league: LeagueView) {
        const memberInfo = league.memberInfo;
        if (memberInfo.isInvited) {
            // accept invitation
            this.invitationService.accept(league.memberInfo.invitationCode, () => {
                this.getLeagues(MODE_JOINED);
                this.getLeagues(MODE_ALL);
            });
        } else if (!memberInfo.isJoinRequested) {
            // create join request
            this.joinRequestService.create(new JoinRequestForm(league.id))
                .subscribe(_ => this.getLeagues(MODE_ALL));
        }
    }

    openLeague(id: number) {
        this.router.navigate(['/leagues/' + id]);
    }
}
