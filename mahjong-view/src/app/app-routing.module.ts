import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {SigninComponent} from './components/signin/signin.component';
import {SignupComponent} from './components/signup/signup.component';
import {GameStartComponent} from './components/game-start/game-start.component';
import {GameListComponent} from './components/game-list/game-list.component';
import {GameComponent} from './components/game/game.component';
import {LeagueListComponent} from './components/league/league-list/league-list.component';
import {LeagueCreateComponent} from './components/league/league-create/league-create.component';
import {LeagueComponent} from './components/league/league/league.component';
import {LeagueInvitationsComponent} from './components/league/league-invitations/league-invitations.component';
import {ProfileComponent} from './components/profile/profile.component';

const routes: Routes = [
    {path: '', redirectTo: '/leagues', pathMatch: 'full'},
    {path: 'signup', component: SignupComponent},
    {path: 'signin', component: SigninComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'games', component: GameListComponent},
    {path: 'games/:id', component: GameComponent},
    {path: 'leagues', component: LeagueListComponent},
    {path: 'leagues/create', component: LeagueCreateComponent},
    {path: 'leagues/invitations', component: LeagueInvitationsComponent},
    {path: 'leagues/:id', component: LeagueComponent},
    {path: 'leagues/:leagueId/start', component: GameStartComponent},
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
