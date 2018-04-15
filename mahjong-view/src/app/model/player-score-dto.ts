export class PlayerScoreDto {

    playerId: number;
    combinationCodes: string[];
    doraCount: number;
    fuCount: number;
    openHand: boolean;
    riichi: boolean;
    tempai: boolean;

    static fromTempai(playerId: number, tempai: boolean, riichi: boolean): PlayerScoreDto {
        let dto = this.fromRiichi(playerId, riichi);
        dto.tempai = tempai;
        return dto;
    }

    static fromWin(playerId: number,
                   combinations: string[],
                   riichi: boolean,
                   doraCount: number,
                   fuCount: number,
                   openHand: boolean): PlayerScoreDto {
        let dto = this.fromRiichi(playerId, riichi);
        dto.combinationCodes = combinations;
        dto.doraCount = doraCount;
        dto.fuCount = fuCount;
        dto.openHand = openHand;
        return dto;
    }

    static fromRiichi(playerId: number, riichi: boolean): PlayerScoreDto {
        let dto = new PlayerScoreDto();
        dto.playerId = playerId;
        dto.combinationCodes = [];
        dto.doraCount = 0;
        dto.fuCount = 0;
        dto.openHand = false;
        dto.riichi = riichi;
        dto.tempai = false;
        return dto;
    }

}