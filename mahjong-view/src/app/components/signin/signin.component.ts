import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
    selector: 'sign-in',
    templateUrl: './signin.component.html',
    styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

    constructor(private userService: AuthService, private router: Router) {
    }

    ngOnInit() {
    }

    signIn(login, password: string) {
        this.userService.signIn(login, password, () => {
            this.router.navigate(['/'])
        });
    }

}
