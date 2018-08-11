package com.github.mahjong.common.jdbc;

import java.util.function.Supplier;

public interface TransactionalHelper {

    void defaultTx(Runnable action);

    <T> T defaultTx(Supplier<T> action);

}
