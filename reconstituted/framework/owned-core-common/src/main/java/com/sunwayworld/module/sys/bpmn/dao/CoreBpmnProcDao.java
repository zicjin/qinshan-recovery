/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import java.util.List;

public interface CoreBpmnProcDao
extends GenericDao<CoreBpmnProcBean, Long> {
    public List<CoreBpmnProcBean> selectRuntimeBpmnProcList(String var1, String var2);

    public List<CoreBpmnProcBean> selectBpmnProcListByServiceId(String var1);
}
