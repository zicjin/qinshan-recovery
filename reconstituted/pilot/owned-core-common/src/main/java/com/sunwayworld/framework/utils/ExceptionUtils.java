/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

public abstract class ExceptionUtils {
    public static final <T extends Throwable> T getCause(Throwable throwable, Class<T> causeClass) {
        int depth = 0;
        for (Throwable temp = throwable; depth++ < 5 && temp != null; temp = temp.getCause()) {
            if (!causeClass.isAssignableFrom(temp.getClass())) continue;
            return (T)temp;
        }
        return null;
    }

    public static Throwable getDeepestCause(Throwable e) {
        while (e != null) {
            Throwable cause = e.getCause();
            if (cause == null) {
                return e;
            }
            e = cause;
        }
        return e;
    }
}
