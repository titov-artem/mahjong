import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PlayerResult} from '../model/player.result';

@Component({
    selector: 'game-player-score',
    templateUrl: './game-player-score.component.html',
    styleUrls: ['./game-player-score.component.css']
})
export class GamePlayerScoreComponent {

    @Output() playerResultUpdatedEvent = new EventEmitter<PlayerResult>();

    @Input() question: string;
    @Input() playerId: number;
    @Input() wind: string;

    @Input()
    set withNone(withNone: boolean) {
        this.withNone_ = withNone;
        if (!withNone) {
            this.result = PlayerResult.RESULT_NO;
        } else {
            this.result = PlayerResult.RESULT_NONE;
        }
    }

    withNone_: boolean;
    result: number = PlayerResult.RESULT_NONE;
    hasRiichi = false;

    yes() {
        if (this.isYes()) {
            this.none();
            return;
        }
        console.log('yes');
        this.result = PlayerResult.RESULT_YES;
        this.emit();
    }

    no() {
        if (this.isNo()) {
            this.none();
            return;
        }
        console.log('no');
        this.result = PlayerResult.RESULT_NO;
        this.emit();
    }

    riichi() {
        console.log('Riichi: ' + this.hasRiichi);
        this.hasRiichi = !this.hasRiichi;
        this.emit();
    }

    private none() {
        console.log('none');
        this.result = PlayerResult.RESULT_NONE;
        this.emit();
    }

    private emit() {
        this.playerResultUpdatedEvent.emit(new PlayerResult(this.playerId, this.result, this.hasRiichi));
    }

    isYes() {
        return this.result == PlayerResult.RESULT_YES;
    }

    isNo() {
        return this.result == PlayerResult.RESULT_NO;
    }

}
