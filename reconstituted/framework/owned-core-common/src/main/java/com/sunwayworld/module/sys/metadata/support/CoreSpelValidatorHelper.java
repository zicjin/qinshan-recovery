/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.support;

import org.springframework.expression.EvaluationContext;

public class CoreSpelValidatorHelper {
    private static final ThreadLocal<EvaluationContext> LOCAL_CONTAINER = new ThreadLocal();

    public static final void bind(EvaluationContext context) {
        LOCAL_CONTAINER.set(context);
    }

    public static final EvaluationContext getBindEvaluationContext() {
        return LOCAL_CONTAINER.get();
    }

    public static final void clear() {
        LOCAL_CONTAINER.remove();
    }
}
