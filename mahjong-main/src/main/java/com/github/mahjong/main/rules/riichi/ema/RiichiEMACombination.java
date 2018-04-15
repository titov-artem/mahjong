package com.github.mahjong.main.rules.riichi.ema;

import com.github.mahjong.main.rules.Combination;

import static com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination.GroupOrderMemorizer.inGroup;

public enum RiichiEMACombination implements Combination {

    RIICHI(1, 0, inGroup(0).onPos(0)),
    TSUMO(1, 0, inGroup(0).onPos(1)),
    IPPATSU(1, 0, inGroup(0).onPos(2)),
    PINFU(1, 0, inGroup(0).onPos(3)),
    TANYAO(1, 1, inGroup(0).onPos(4)),
    YAKUHAI_1(1, 1, inGroup(0).onPos(5)),
    YAKUHAI_2(2, 2, inGroup(0).onPos(6)),

    CHIITOITSU(2, 0, inGroup(1).onPos(0)),
    IIPEIKOU(1, 0, inGroup(1).onPos(1)),
    SANSHOKU(2, 1, inGroup(1).onPos(2)),
    ITTSUU(2, 1, inGroup(1).onPos(3)),
    TOITOIHOU(2, inGroup(1).onPos(4)),
    SANANKOU(2, 1, inGroup(1).onPos(5)),
    CHANTA(2, 1, inGroup(1).onPos(6)),
    JUNCHAN(3, 2, inGroup(1).onPos(7)),
    HONROUTOU(2, 2, inGroup(1).onPos(8)),
    HONITSU(3, 2, inGroup(1).onPos(9)),
    CHINITSU(6, 5, inGroup(1).onPos(10)),

    HAITEI(1, 1, inGroup(2).onPos(0)),
    HOUTEI(1, 1, inGroup(2).onPos(1)),
    RINSHAN(1, 1, inGroup(2).onPos(2)),
    CHANKAN(1, 1, inGroup(2).onPos(3)),
    DABURII(2, 0, inGroup(2).onPos(4)),


    RYANPEIKOU(3, 0, inGroup(3).onPos(0)),
    SANSHOKU_DOUKOU(2, 2, inGroup(3).onPos(1)),
    SAN_KANTSU(2, 2, inGroup(3).onPos(2)),
    YAKUHAI_3(3, 3, inGroup(3).onPos(3)),
    YAKUHAI_4(4, 4, inGroup(3).onPos(4)),
    SHOUSANGEN(2, 2, inGroup(3).onPos(5)),

    KOKUSHI_MUSOU(SCORE_LIMIT, inGroup(4).onPos(0)),
    SUU_ANKOU(SCORE_LIMIT, inGroup(4).onPos(1)),
    DAISANGEN(SCORE_LIMIT, inGroup(4).onPos(2)),
    SHOUSUUSHII(SCORE_LIMIT, inGroup(4).onPos(3)),
    DAISUUSHII(SCORE_LIMIT, inGroup(4).onPos(4)),
    TSUUIISOU(SCORE_LIMIT, inGroup(4).onPos(5)),
    CHINROUTOU(SCORE_LIMIT, inGroup(4).onPos(6)),
    RYUUIISOU(SCORE_LIMIT, inGroup(4).onPos(7)),
    CHUUREN_POUTOU(SCORE_LIMIT, inGroup(4).onPos(8)),
    SUU_KANTSU(SCORE_LIMIT, inGroup(4).onPos(9)),
    TENHOU(SCORE_LIMIT, inGroup(4).onPos(10)),
    CHIIHOU(SCORE_LIMIT, inGroup(4).onPos(11));

    private final int closeHandScore;
    private final int openHandScore;
    private final int group;
    private final int order;

    RiichiEMACombination(int closeHandScore, int openHandScore, GroupOrderMemorizer memorizer) {
        this.closeHandScore = closeHandScore;
        this.openHandScore = openHandScore;
        this.group = memorizer.getGroup();
        this.order = memorizer.getOrder();
    }

    RiichiEMACombination(int handScore, GroupOrderMemorizer memorizer) {
        this.closeHandScore = handScore;
        this.openHandScore = handScore;
        this.group = memorizer.getGroup();
        this.order = memorizer.getOrder();
    }

    @Override
    public String getCode() {
        return String.format("%s_%s", RiichiEMARuleSet.RIICHI_EMA_RULES_CODE, name());
    }

    @Override
    public int getScore(boolean openHand) {
        return openHand ? openHandScore : closeHandScore;
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public static RiichiEMACombination forCode(String code) {
        for (RiichiEMACombination c : values()) {
            if (c.getCode().equals(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    static final class GroupOrderMemorizer {
        private int group;
        private int order;

        static GroupOrderMemorizer inGroup(int group) {
            GroupOrderMemorizer memorizer = new GroupOrderMemorizer();
            memorizer.group = group;
            return memorizer;
        }

        GroupOrderMemorizer onPos(int order) {
            this.order = order;
            return this;
        }

        public int getGroup() {
            return group;
        }

        public int getOrder() {
            return order;
        }
    }
}
