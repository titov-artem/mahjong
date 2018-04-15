package com.github.mahjong.security.repo.jdbc;

import com.github.mahjong.security.repo.TransactionalHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class AnnotationBasedTransactionalHelper implements TransactionalHelper {

    @Transactional
    @Override
    public void defaultTx(Runnable action) {
        action.run();
    }

    @Transactional
    @Override
    public <T> T defaultTx(Supplier<T> action) {
        return action.get();
    }
}
