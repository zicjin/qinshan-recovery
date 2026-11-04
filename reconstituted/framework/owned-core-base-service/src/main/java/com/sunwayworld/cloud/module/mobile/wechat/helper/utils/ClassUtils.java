/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

public abstract class ClassUtils {
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, true, classLoader);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}
