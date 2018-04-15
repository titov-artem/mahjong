import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {LeagueService} from '../../../services/league.service';
import {EnumsService} from '../../../services/enums.service';
import {NgForm} from '@angular/forms';
import {LeagueForm} from '../../../model/league/league-form';

@Component({
    selector: 'league-create',
    templateUrl: './league-create.component.html',
    styleUrls: ['./league-create.component.css']
})
export class LeagueCreateComponent implements OnInit {

    langs: string[];

    constructor(private router: Router,
                private leagueService: LeagueService,
                private enumsService: EnumsService) {
    }

    ngOnInit() {
        this.getLangs();
    }

    private getLangs() {
        this.enumsService.getSupportedLangs().subscribe(langs => this.langs = langs);
    }

    create(form: NgForm) {
        console.log(form.value);
        let name = {};
        let description = {};
        for (let lang of this.langs) {
            if (form.value.hasOwnProperty(this.getFormNameProp(lang))) {
                name[lang] = form.value[this.getFormNameProp(lang)];
            }
            if (form.value.hasOwnProperty(this.getFormDescriptionProp(lang))) {
                description[lang] = form.value[this.getFormDescriptionProp(lang)];
            }
        }
        let leagueForm = new LeagueForm();
        leagueForm.name = name;
        leagueForm.description = description;
        leagueForm.admins = [];
        this.leagueService.create(leagueForm).subscribe(_ => { this.router.navigate(["leagues"])});
    }

    private getFormNameProp(lang) {
        return 'name_' + lang;
    }

    private getFormDescriptionProp(lang) {
        return 'description_' + lang;
    }
}
