/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import java.util.List;

public interface CoreBpmnInstanceTaskHisService
extends GenericService<CoreBpmnInstanceTaskHisBean, Long> {
    public List<CoreBpmnInstanceTaskHisBean> selectLatestTaskHisList(List<CoreBpmnInstanceTaskHisBean> var1);
}
