/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring;

import com.sunwayworld.framework.utils.SpringUtils;

public class SpringCaller {
    private final Object springBean;
    private final String methodName;

    private SpringCaller(Object springBean, String methodName) {
        this.springBean = springBean;
        this.methodName = methodName;
    }

    public static SpringCaller of(Object springBean, String methodName) {
        return new SpringCaller(springBean, methodName);
    }

    public Object call(Object ... args) {
        return SpringUtils.invoke(this.springBean, this.methodName, args);
    }
}
