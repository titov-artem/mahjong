import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppBootstrapModule} from './modules/app-bootstrap.module';

import {AppComponent} from './app.component';
import {Globals} from './globals';

import {CookieService} from 'ngx-cookie-service';
import {AuthService} from './services/auth.service';
import {PlayerService} from './services/player.service';
import {GameService} from './services/game.service';
import {RulesSetService} from './services/rules-set.service';
import {LeagueService} from './services/league.service';
import {EnumsService} from './services/enums.service';
import {LeagueJoinRequestService} from './services/league-join-request.service';
import {LeagueInvitationService} from './services/league-invitation.service';

import {SignupComponent} from './components/signup/signup.component';
import {SigninComponent} from './components/signin/signin.component';
import {GameStartComponent} from './components/game-start/game-start.component';
import {PlayerSelectComponent} from './components/game-start/player-select/player-select.component';
import {GameListComponent} from './components/game-list/game-list.component';
import {GameComponent} from './components/game/game.component';
import {RiichiStickComponent} from './components/game/riichi/riichi-stick.component';
import {HonbaStickComponent} from './components/game/honba/honba-stick.component';
import {GamePlayerComponent} from './components/game/player/game-player.component';
import {GamePlayerScoreComponent} from './components/game/player-score/game-player-score.component';
import {CombinationSelectComponent} from './components/game/combination-select/combination-select.component';
import {LeagueListComponent} from './components/league/league-list/league-list.component';
import {LeagueCreateComponent} from './components/league/league-create/league-create.component';
import {LeagueComponent} from './components/league/league/league.component';
import {LeagueInviteComponent} from './components/league/league-invite/league-invite.component';
import {LeagueInvitationsComponent} from './components/league/league-invitations/league-invitations.component';
import {AlertComponent} from './components/alert/alert.component';
import {AlertItemComponent} from './components/alert/alert-item/alert-item.component';
import {ProfileComponent} from './components/profile/profile.component';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        AppRoutingModule,
        HttpClientModule,
        ReactiveFormsModule,
        AppBootstrapModule,
    ],
    entryComponents: [
        AlertItemComponent,
    ],
    declarations: [
        AppComponent,
        AlertComponent,
        AlertItemComponent,

        /* Mahjong components */
        SignupComponent,
        SigninComponent,
        ProfileComponent,
        GameListComponent,
        GameComponent,
        GameStartComponent,
        PlayerSelectComponent,

        RiichiStickComponent,
        HonbaStickComponent,
        GamePlayerComponent,
        GamePlayerScoreComponent,
        CombinationSelectComponent,

        LeagueListComponent,
        LeagueCreateComponent,
        LeagueComponent,
        LeagueInviteComponent,
        LeagueInvitationsComponent,
    ],
    providers: [
        Globals,

        CookieService,
        AuthService,
        PlayerService,
        GameService,
        RulesSetService,
        EnumsService,
        LeagueService,
        LeagueJoinRequestService,
        LeagueInvitationService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
