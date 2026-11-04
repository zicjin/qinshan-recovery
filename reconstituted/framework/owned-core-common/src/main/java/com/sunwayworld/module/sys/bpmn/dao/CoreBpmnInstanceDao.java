/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceDetailedCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskCandidatorDTO;
import java.util.List;

public interface CoreBpmnInstanceDao
extends GenericDao<CoreBpmnInstanceBean, Long> {
    public List<CoreBpmnInstanceCandidatorDTO> selectInstanceCandidatorList(List<Long> var1);

    public List<CoreBpmnInstanceTaskCandidatorDTO> selectInstanceTaskAuditorList(List<Long> var1);

    public List<CoreBpmnInstanceDetailedCandidatorDTO> selectInstanceDetailedCandidatorList(List<Long> var1);
}
