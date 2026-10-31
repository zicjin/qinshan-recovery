package com.sunwayworld.framework.exception;

import java.util.function.Supplier;

/** Executes optional work while suppressing ordinary exceptions. */
public abstract class QuietHelper {
    public static void call(Runnable runnable) {
        try { runnable.run(); } catch (Exception ignored) { }
    }
    public static <T> T call(Supplier<T> supplier) {
        try { return supplier.get(); } catch (Exception ignored) { return null; }
    }
}
