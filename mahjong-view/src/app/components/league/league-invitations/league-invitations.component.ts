import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {LeagueInvitationService} from '../../../services/league-invitation.service';
import {InvitationView} from '../../../model/league/invitation-view';

@Component({
    selector: 'league-invitations',
    templateUrl: './league-invitations.component.html',
    styleUrls: ['./league-invitations.component.css']
})
export class LeagueInvitationsComponent implements OnInit {

    invitations: InvitationView[];

    constructor(private router: Router,
                private invitationService: LeagueInvitationService) {
    }

    ngOnInit() {
        this.getInvitations();
    }

    getInvitations() {
        this.invitationService.getInvitations()
            .subscribe(invitations => this.invitations = invitations);
    }

    accept(invitation: InvitationView) {
        this.invitationService.accept(invitation.code, () => {
            this.getInvitations();
        })
    }

    reject(invitation: InvitationView) {
        this.invitationService.reject(invitation.code, () => {
            this.getInvitations();
        })
    }
}
