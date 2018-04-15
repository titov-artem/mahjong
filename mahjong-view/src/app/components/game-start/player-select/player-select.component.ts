import {AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {PlayerShort} from '../../../model/player.short';
import {toNumber} from 'ngx-bootstrap/timepicker/timepicker.utils';
import {PlayerSelectUpdated} from '../model/player.select.updated';

declare var $: any;

@Component({
    selector: 'player-select',
    templateUrl: './player-select.component.html',
})
export class PlayerSelectComponent implements AfterViewInit, OnChanges {
    @Input() players: PlayerShort[];

    @Input() id: string;
    @Output() selectionUpdatedEvent = new EventEmitter<PlayerSelectUpdated>();

    private player: PlayerShort = null;
    private wind: string = null;

    ngAfterViewInit(): void {
        let comp = this;
        this.playerSelect().selectpicker({
            liveSearch: true,
            liveSearchNormalize: true,
            width: 'auto',
        }).on('changed.bs.select', function () {
            comp.emitEvent();
        });
        this.windSelect().selectpicker({
            style: 'btn-info',
            width: 'auto',
        }).on('changed.bs.select', function () {
            comp.emitEvent();
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        setTimeout(() => {
            this.playerSelect().selectpicker('refresh');
            console.log(this.players);
            if (this.players.length > 0) {
                this.emitEvent();
            }
        }, 250);
    }

    private playerSelect() {
        return $(`#${this.id}`);
    }

    private windSelect() {
        return $(`#${this.id}-wind`);
    }

    emitEvent() {
        let selectedId = this.playerSelect().find('option:selected').val();
        let selectedName = this.playerSelect().find('option:selected').text();
        let player = new PlayerShort();
        player.id = toNumber(selectedId);
        player.name = selectedName;

        let wind = this.windSelect().find('option:selected').val();

        let event = new PlayerSelectUpdated(player, wind, this.player, this.wind);
        this.player = player;
        this.wind = wind;
        console.log('Emit event');
        console.log(event);
        this.selectionUpdatedEvent.emit(event)
    }

}
