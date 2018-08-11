package com.github.mahjong.common.jdbc;

import java.util.function.Supplier;

/**
 * Do nothing except executing specified actions
 */
public class DummyTxHelper implements TransactionalHelper {
    @Override
    public void defaultTx(Runnable action) {
        action.run();
    }

    @Override
    public <T> T defaultTx(Supplier<T> action) {
        return action.get();
    }
}
