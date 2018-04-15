import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LeagueView} from '../../../model/league/league-view';
import {LeagueService} from '../../../services/league.service';
import {LeagueJoinRequestService} from '../../../services/league-join-request.service';
import {JoinRequestForm} from '../../../model/league/join-request-form';
import {LeagueInvitationService} from '../../../services/league-invitation.service';
import {PlayerService} from '../../../services/player.service';
import {PlayerShort} from '../../../model/player.short';
import {Globals} from '../../../globals';
import {JoinRequestView} from '../../../model/league/join-request-view';
import {JoinRequestRejectForm} from '../../../model/league/join-request-reject-form';

@Component({
    selector: 'league',
    templateUrl: './league.component.html',
    styleUrls: ['./league.component.css']
})
export class LeagueComponent implements OnInit {

    leagueId: number;
    league: LeagueView;
    players: PlayerShort[] = [];
    allPlayers: PlayerShort[] = [];

    isAdmin: boolean = false;
    joinRequests: JoinRequestView[] = [];

    constructor(private route: ActivatedRoute,
                private router: Router,
                private leagueService: LeagueService,
                private playerService: PlayerService,
                private joinRequestService: LeagueJoinRequestService,
                private invitationService: LeagueInvitationService,
                private globals: Globals) {
    }

    ngOnInit() {
        this.leagueId = +this.route.snapshot.paramMap.get('id');
        this.getLeague();
        this.getLeaguePlayers();
        this.getAllPlayers();
    }

    private getLeague() {
        this.leagueService.getLeague(this.leagueId)
            .subscribe(league => {
                this.league = league;
                if (league.admins.indexOf(this.globals.player.id) != -1) {
                    this.isAdmin = true;
                    this.getJoinRequests();
                }
            });
    }

    private getLeaguePlayers() {
        this.playerService.getLeaguePlayers(this.leagueId)
            .subscribe(players => this.players = players);
    }

    private getAllPlayers() {
        this.playerService.getPlayers()
            .subscribe(players => this.allPlayers = players);
    }

    private getJoinRequests() {
        this.joinRequestService.getIngoing(this.leagueId)
            .subscribe(requests => this.joinRequests = requests);
    }

    joinLeague(league: LeagueView) {
        const memberInfo = league.memberInfo;
        if (memberInfo.isInvited) {
            // accept invitation
            this.invitationService.accept(league.memberInfo.invitationCode, () => {
                // todo show some message and buttons
                this.getLeague();
            });
        } else if (!memberInfo.isJoinRequested) {
            // create join request
            this.joinRequestService.create(new JoinRequestForm(league.id))
                .subscribe(_ => {
                    // todo show some message, that reequest sent
                    this.getLeague();
                });
        }
    }

    /* Admin functions */
    approveJoinRequest(req: JoinRequestView) {
        this.joinRequestService.approve(req.id, () => {
            this.getJoinRequests();
            this.getLeaguePlayers();
        })
    }

    rejectJoinRequest(req: JoinRequestView) {
        // todo add ability to enter reject reason
        this.joinRequestService.reject(req.id, new JoinRequestRejectForm(req.id, ''), () => {
            this.getJoinRequests();
        })
    }
}
