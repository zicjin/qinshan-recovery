/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.boot;

import com.sunwayworld.cloud.module.script.computing.formula.CoreFormulaRepository;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.utils.ArithUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class CoreScriptApplicationListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        GikamFormulaRegistry.INSTANCE.register(CoreFormulaRepository.class);
        ArithUtils.eval("0+0");
    }
}
