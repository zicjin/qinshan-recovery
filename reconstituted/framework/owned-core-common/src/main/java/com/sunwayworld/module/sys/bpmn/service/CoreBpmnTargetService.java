/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;

public interface CoreBpmnTargetService
extends GenericService<CoreBpmnTargetBean, String> {
    public String getLcdpUrl(String var1);
}
