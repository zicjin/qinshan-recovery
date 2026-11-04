/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mytask.service.impl;

import com.sunwayworld.cloud.module.mytask.bean.CoreMyTaskBean;
import com.sunwayworld.cloud.module.mytask.persistent.dao.CoreMyTaskDao;
import com.sunwayworld.cloud.module.mytask.service.CoreMyTaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMyTaskServiceImpl
implements CoreMyTaskService {
    @Autowired
    private CoreMyTaskDao coreMyTaskDao;
    @Lazy
    @Autowired
    private CoreMyTaskService coreMyTaskService;

    @Override
    public CoreMyTaskDao getDao() {
        return this.coreMyTaskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreMyTaskBean myTask = wrapper.parseUnique(CoreMyTaskBean.class);
        if (myTask.getId() == null) {
            myTask.setId(ApplicationContextHelper.getNextIdentity());
        }
        this.getDao().insert(myTask);
        return myTask.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(CoreMyTaskBean myTask) {
        myTask.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(myTask);
        return myTask.getId();
    }
}
