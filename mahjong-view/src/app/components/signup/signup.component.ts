import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {PlayerService} from '../../services/player.service';
import {Player} from '../../model/player';
import {Router} from '@angular/router';
import {NgForm} from '@angular/forms';
import {EnumsService} from '../../services/enums.service';

declare var $: any;

@Component({
    selector: 'sign-out',
    templateUrl: './signup.component.html',
    styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit, AfterViewChecked {

    @ViewChild('langSelect') langSelect: ElementRef = null;

    langs: string[];

    constructor(private userAuthService: AuthService,
                private playerService: PlayerService,
                private enumsService: EnumsService,
                private router: Router) {
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

    signUp(form: NgForm) {
        console.log('Creating user');
        this.userAuthService.signUp(form.value.login, form.value.password).subscribe(_ => {
            this.userAuthService.signIn(form.value.login, form.value.password, () => {
                let player = new Player();
                player.login = form.value.login;
                player.name = form.value.name;
                player.lang = form.value.lang;
                // todo fix: current player not found, because it is not created before request
                this.playerService.createPlayer(player).subscribe(_ => {
                    this.router.navigate(["/"])
                });
            });
        });

    }

}
