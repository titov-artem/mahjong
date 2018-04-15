import {Player} from './model/player';
import {EventEmitter, Injectable} from '@angular/core';
import {ErrorView} from './model/error-view';

@Injectable()
export class Globals {
    player: Player;

    errorEventEmitter = new EventEmitter<ErrorView>();
    messageEventEmitter = new EventEmitter<string>();

    emitError(err: ErrorView) {
        this.errorEventEmitter.emit(err);
    }

    emitMessage(message: string) {
        this.messageEventEmitter.emit(message);
    }
}