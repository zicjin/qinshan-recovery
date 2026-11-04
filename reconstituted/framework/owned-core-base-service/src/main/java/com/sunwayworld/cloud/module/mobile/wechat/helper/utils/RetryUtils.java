/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import java.util.concurrent.Callable;

public class RetryUtils {
    public static <V extends ResultCheck> V retryOnException(int retryLimit, Callable<V> retryCallable) {
        ResultCheck v = null;
        for (int i = 0; i < retryLimit; ++i) {
            try {
                v = (ResultCheck)retryCallable.call();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (null != v && v.matching()) break;
        }
        return (V)v;
    }

    public static <V extends ResultCheck> V retryOnException(int retryLimit, long sleepMillis, Callable<V> retryCallable) throws InterruptedException {
        ResultCheck v = null;
        for (int i = 0; i < retryLimit; ++i) {
            try {
                v = (ResultCheck)retryCallable.call();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (null != v && v.matching()) break;
            Thread.sleep(sleepMillis);
        }
        return (V)v;
    }

    public static interface ResultCheck {
        public boolean matching();

        public String getJson();
    }
}
