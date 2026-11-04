/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreBpmnInstanceTaskDao
extends GenericDao<CoreBpmnInstanceTaskBean, Long> {
    public List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(@Nullable String var1, List<Long> var2);
}
