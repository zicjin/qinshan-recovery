/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class TransactionUtils {
    private static final Logger logger = LogManager.getLogger(TransactionUtils.class);

    public static void transactionRequired() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new ApplicationRuntimeException("TRANSACTION.ERROR.TRANSACTION_REQUIRED", new String[0]);
        }
    }

    public static final void runAfterCommit(final Runnable runnable) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.warn("No transaction available, unable run after commit.");
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            @Override
            public void afterCommit() {
                runnable.run();
            }
        });
    }

    public static final void runBeforeCommit(final Runnable runnable) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.warn("No transaction available, unable run before commit.");
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            @Override
            public void beforeCommit(boolean readOnly) {
                runnable.run();
            }
        });
    }

    public static final void runAfterCompletion(final Runnable runnable) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.warn("No transaction available, unable run after completion.");
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            @Override
            public void afterCompletion(int status) {
                runnable.run();
            }
        });
    }

    public static final void runAfterCompletion(final Consumer<Integer> consumer) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.warn("No transaction available, unable run after completion.");
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            @Override
            public void afterCompletion(int status) {
                consumer.accept(status);
            }
        });
    }
}
