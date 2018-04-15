import {Component, OnInit} from '@angular/core';
import {AuthService} from './services/auth.service';
import {PlayerService} from './services/player.service';
import {Player} from './model/player';
import {Globals} from './globals';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    authenticated: boolean = false;
    player: Player;

    constructor(private authService: AuthService,
                private playerService: PlayerService,
                private globals: Globals) {
        authService.userAuthenticated.subscribe(authenticated => {
            this.authenticated = authenticated;
            if (authenticated) {
                this.getCurrentUser();
            } else {
                this.player = null;
                this.globals.player = null;
            }
        })
    }

    ngOnInit(): void {
        this.getCurrentUser();
    }

    private getCurrentUser() {
        this.playerService.getCurrent().subscribe(player => {
            this.player = player;
            this.globals.player = player;
            this.authenticated = true;
        });
    }

    signOut() {
        this.authService.signOut();
    }

    toggleMenu(menuToggleButton: HTMLButtonElement) {
        if (menuToggleButton.getAttribute('aria-expanded') == 'true') {
            menuToggleButton.click();
        }
    }
}
