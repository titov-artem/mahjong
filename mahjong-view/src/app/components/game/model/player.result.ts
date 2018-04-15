export class PlayerResult {

    static readonly RESULT_YES = 1;
    static readonly RESULT_NO = -1;
    static readonly RESULT_NONE = 0;

    constructor(public playerId: number, public result: number, public hasRiichi: boolean) {
    }

    isYes() {
        return this.result == PlayerResult.RESULT_YES;
    }

    isNo() {
        return this.result == PlayerResult.RESULT_NO;
    }

    isNone() {
        return this.result == PlayerResult.RESULT_NONE;
    }
}