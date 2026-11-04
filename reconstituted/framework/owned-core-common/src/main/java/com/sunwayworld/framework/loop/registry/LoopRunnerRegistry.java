/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.loop.registry;

import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.ArrayList;
import java.util.List;

public enum LoopRunnerRegistry {
    INSTANCE;

    private List<LoopRunner> runnerContainer = new ArrayList<LoopRunner>();

    public void add(Class<? extends LoopRunner> loopRunnerClazz) {
        LoopRunner runner;
        if (this.runnerContainer.stream().noneMatch(r -> loopRunnerClazz.equals(r.getClass())) && (runner = ClassUtils.newInstance(loopRunnerClazz)).enable()) {
            this.runnerContainer.add(runner);
        }
    }

    public List<LoopRunner> getRunnerContainer() {
        return this.runnerContainer;
    }
}
