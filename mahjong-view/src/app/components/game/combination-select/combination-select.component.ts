import {AfterViewInit, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {RulesSetService} from '../../../services/rules-set.service';
import {CombinationView} from '../../../model/combination-view';
import {PlayerResult} from '../model/player.result';
import {PlayerCombinations} from '../model/player.combinations';
import {CombinationsGroupView} from '../../../model/combinations-group-view';
import {CombinationsView} from '../../../model/combinations-view';

declare var $: any;

@Component({
    selector: 'combination-select',
    templateUrl: './combination-select.component.html',
    styleUrls: ['./combination-select.component.css']
})
export class CombinationSelectComponent implements OnInit, AfterViewInit {

    @Output() combinationsSelectedEvent = new EventEmitter<PlayerCombinations>();

    combinations: CombinationsView = null;
    availableCombinations = new Set<string>();
    isOpenHand = false;

    playerCombinations = new Set<string>();

    constructor(private rulesSetService: RulesSetService) {
    }

    ngOnInit(): void {
        this.getCombinations();
    }

    ngAfterViewInit(): void {
        $('select.selectpicker').selectpicker({
            liveSearch: true,
            liveSearchNormalize: true,
            // width: 'auto',
        })
    }

    private getCombinations() {
        this.rulesSetService.get('RIICHI_EMA')
            .subscribe(rulesSet => {
                this.combinations = rulesSet.combinations;
                this.openHand(false);
            });
    }

    range(from, to, step: number): number[] {
        let out = [];
        for (let i = from; i <= to; i += step) {
            out.push(i);
        }
        return out;
    }

    combinationsGrid(): CombinationView[][][] {
        let out = [];
        let group = [];
        for (let g of this.combinations.groups) {
            let row = [];
            for (let c of g.combinations) {
                if (!this.availableCombinations.has(c.code)) {
                    continue;
                }
                row.push(c);
                if (row.length == 3) {
                    group.push(row);
                    row = [];
                }
            }
            if (row.length > 0) {
                group.push(row);
            }
            if (group.length > 0) {
                out.push(group);
            }
            group = [];
        }
        return out;
    }

    openHand(isOpenHand: boolean) {
        this.isOpenHand = isOpenHand;
        if (this.combinations == null) {
            return;
        }
        this.availableCombinations.clear();
        for (let g of this.combinations.groups) {
            for (let c of g.combinations) {
                if (this.isOpenHand) {
                    if (c.openPrice > 0) {
                        this.availableCombinations.add(c.code);
                    } else {
                        this.playerCombinations.delete(c.code);
                    }
                } else if (!this.isOpenHand) {
                    if (c.closePrice > 0) {
                        this.availableCombinations.add(c.code);
                    } else {
                        this.playerCombinations.delete(c.code);
                    }
                }
            }
        }
    }

    toggleCombination(code: string) {
        if (this.playerCombinations.has(code)) {
            this.playerCombinations.delete(code);
        } else {
            this.playerCombinations.add(code);
        }
    }

    hasCombination(code: string) {
        return this.playerCombinations.has(code);
    }

    private doraSelect() {
        return $(`#dora-count`);
    }

    private fuSelect() {
        return $(`#fu-count`);
    }

    done() {
        console.log(this.playerCombinations);
        this.combinationsSelectedEvent.emit(new PlayerCombinations(
            Array.from(this.playerCombinations),
            this.doraSelect().find('option:selected').val(),
            this.fuSelect().find('option:selected').val(),
            this.isOpenHand
        ));
    }
}
