/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CoreBpmnInstanceTaskHisDao
extends GenericDao<CoreBpmnInstanceTaskHisBean, Long> {
    public List<CoreBpmnInstanceTaskHisBean> selectLatestTaskHisList(@Param(value="filterList") List<CoreBpmnInstanceTaskHisBean> var1);
}
