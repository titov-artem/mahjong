package com.github.mahjong.main.rules.riichi;

public class RiichiBasicScoreHelper {

    public static int getBasicPoints(int han, int fu) {
        int basic = getBasicScore(han, fu);
        if (basic <= 2000) {
            return basic;
        }
        basic = 2000;

        switch (han) {
            case 0:
            case 1:
            case 2:
                throw new IllegalStateException("Can't have mangan with <= 2 han");
            case 3:
            case 4:
            case 5:
                return basic;
            case 6:
            case 7:
                return (int) (1.5 * basic);
            case 8:
            case 9:
            case 10:
                return 2 * basic;
            case 11:
            case 12:
                return 3 * basic;
            default:
                return 4 * basic;
        }
    }

    private static int getBasicScore(int han, int fu) {
        return fu * (int) Math.pow(2, (2 + han));
    }

    public static int roundPoints(int score) {
        if (score % 100 == 0) return score;
        return (score / 100) * 100 + 100;
    }

}
