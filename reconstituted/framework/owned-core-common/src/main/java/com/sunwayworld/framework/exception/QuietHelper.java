/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class QuietHelper {
    private static final Logger logger = LogManager.getLogger(QuietHelper.class);

    public static void call(Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception ex) {
            logger.debug(ex.getMessage(), (Throwable)ex);
        }
    }

    public static <T> T call(Supplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception ex) {
            logger.debug(ex.getMessage(), (Throwable)ex);
            return null;
        }
    }
}
