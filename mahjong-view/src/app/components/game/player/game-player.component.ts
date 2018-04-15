import {Component, Input} from '@angular/core';

@Component({
    selector: 'game-player',
    templateUrl: './game-player.component.html',
    styleUrls: ['./game-player.component.css']
})
export class GamePlayerComponent {

    @Input() name: string;
    @Input() score: number;
    @Input() wind: string;

}
