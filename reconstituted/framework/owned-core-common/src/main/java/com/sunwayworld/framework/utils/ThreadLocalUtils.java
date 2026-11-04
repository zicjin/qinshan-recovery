/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadLocalUtils {
    private static final Logger logger = LogManager.getLogger(ThreadLocalUtils.class);

    public static void printThreadLocalContent() {
        ThreadLocal t = new ThreadLocal();
        Method m = ReflectionUtils.findMethod(ThreadLocal.class, "getMap", Thread.class);
        Thread currentThread = Thread.currentThread();
        Object map = ReflectionUtils.invokeMethod(m, t, currentThread);
        Object entries = ReflectionUtils.getFieldValue(map, "table");
        int qty = Array.getLength(entries);
        StringBuilder builder = new StringBuilder("=================== ThreadLocal Content List (Thread ID : ").append(currentThread.getId()).append(") BEGIN ===================\n");
        for (int i = 0; i < qty; ++i) {
            Object ob = Array.get(entries, i);
            if (ob == null) continue;
            Object value = ReflectionUtils.getFieldValue(ob, "value");
            if (value != null) {
                builder.append(ReflectionUtils.getFieldValue(ob, "referent") + ":" + value.getClass() + "---" + ObjectUtils.toShortString(value));
            } else {
                builder.append(ReflectionUtils.getFieldValue(ob, "referent") + ":" + value);
            }
            builder.append("\n");
        }
        builder.append("=================== ThreadLocal Content List (Thread ID : ").append(currentThread.getId()).append(") END   ===================");
        logger.info(builder.toString());
    }
}
