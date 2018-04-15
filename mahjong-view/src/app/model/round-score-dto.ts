import {PlayerScoreDto} from './player-score-dto';

export class RoundScoreDto {

    scores: PlayerScoreDto[];
    winners: number[];
    losers: number[];

    static from(scores: PlayerScoreDto[], winners: number[], losers: number[]): RoundScoreDto {
        let dto = new RoundScoreDto();
        dto.scores = scores;
        dto.winners = winners;
        dto.losers = losers;
        return dto;
    }
}