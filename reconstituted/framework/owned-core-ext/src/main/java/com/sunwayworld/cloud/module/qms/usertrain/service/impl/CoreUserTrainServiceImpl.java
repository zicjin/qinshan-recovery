/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.service.impl;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.cloud.module.qms.usertrain.persistent.dao.CoreUserTrainDao;
import com.sunwayworld.cloud.module.qms.usertrain.service.CoreUserTrainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreUserTrainServiceImpl
implements CoreUserTrainService {
    @Autowired
    private CoreUserTrainDao coreUserTrainDao;
    @Autowired
    @Lazy
    private CoreBpmnInstanceService coreBpmnInstanceService;
    @Autowired
    @Lazy
    private CoreBpmnInstanceTaskService coreBpmnInstanceTaskService;

    @Override
    public CoreUserTrainDao getDao() {
        return this.coreUserTrainDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreUserTrainBean coreUserTrain = jsonWrapper.parseUnique(CoreUserTrainBean.class);
        coreUserTrain.setId(ApplicationContextHelper.getNextIdentity());
        coreUserTrain.setTrainNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "RYPX"));
        this.getDao().insert(coreUserTrain);
        return coreUserTrain.getId();
    }
}
