package com.github.mahjong.league.repo;

import java.util.function.Supplier;

public interface TransactionalHelper {

    void defaultTx(Runnable action);

    <T> T defaultTx(Supplier<T> action);

}
