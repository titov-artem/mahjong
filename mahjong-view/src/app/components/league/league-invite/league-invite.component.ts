import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {PlayerShort} from '../../../model/player.short';
import {PlayerService} from '../../../services/player.service';
import {LeagueInvitationService} from '../../../services/league-invitation.service';
import {InvitationForm} from '../../../model/league/invitation-form';

declare var $: any;

@Component({
    selector: 'league-invite',
    templateUrl: './league-invite.component.html',
    styleUrls: ['./league-invite.component.css']
})
export class LeagueInviteComponent implements OnInit {

    @Input() leagueId: number;
    @Input() players: PlayerShort[];

    @ViewChild('playerSelect') playerSelect: ElementRef;

    showForm: boolean = false;

    constructor(private router: Router,
                private playerService: PlayerService,
                private invitationService: LeagueInvitationService) {
    }

    ngOnInit() {
        $(this.playerSelect.nativeElement).selectpicker({
            liveSearch: true,
            liveSearchNormalize: true,
            width: 'auto',
        })
    }

    toggleForm() {
        this.showForm = !this.showForm;
        if (this.showForm) {
            console.log(this.players);
            $(this.playerSelect.nativeElement).selectpicker('refresh');
        }
    }

    invite() {
        let playerId = +$(this.playerSelect.nativeElement).find('option:selected').val();
        console.log('Inviting player: ' + playerId);
        this.invitationService.create(new InvitationForm(this.leagueId, playerId))
            .subscribe(_ => {
                this.toggleForm();
            })
    }

}
