import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Globals} from '../../globals';
import {PlayerService} from '../../services/player.service';
import {NgForm} from '@angular/forms';
import {EnumsService} from '../../services/enums.service';
import {Player} from '../../model/player';

declare var $: any;

@Component({
    selector: 'profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, AfterViewChecked {

    @ViewChild('langSelect') langSelect: ElementRef = null;

    langs: string[];

    constructor(private playerService: PlayerService,
                private enumsService: EnumsService,
                private globals: Globals) {
    }

    ngOnInit() {
        this.getLangs();
    }

    ngAfterViewChecked(): void {
        if (this.langSelect == null) {
            return;
        }
        $(this.langSelect.nativeElement).selectpicker('refresh');
    }

    private getLangs() {
        this.enumsService.getSupportedLangs().subscribe(langs => {
            this.langs = langs;
        });
    }

    getPlayer() {
        return this.globals.player;
    }

    updateProfile(form: NgForm) {
        console.log(form);
        let player = new Player();
        player.id = this.getPlayer().id;
        player.login = this.getPlayer().login;
        player.name = form.value.name;
        player.lang = form.value.lang;
        console.log(player);
        this.playerService.updatePlayer(player).subscribe(
            player => {
                this.globals.player = player;
                this.globals.emitMessage("Player updated");
            }
        );
    }

}
