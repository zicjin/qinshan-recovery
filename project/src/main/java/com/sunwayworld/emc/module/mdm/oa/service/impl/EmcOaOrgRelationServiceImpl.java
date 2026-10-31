// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.service.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaOrgRelationBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaOrgRelationDao;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaOrgRelationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcOaOrgRelationServiceImpl
implements EmcOaOrgRelationService {
    @Autowired
    private EmcOaOrgRelationDao emcOaOrgRelationDao;

    @Override
    public EmcOaOrgRelationDao getDao() {
        return this.emcOaOrgRelationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOaOrgRelationBean emcOaOrgRelation = jsonWrapper.parseUnique(EmcOaOrgRelationBean.class);
        emcOaOrgRelation.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcOaOrgRelation);
        return emcOaOrgRelation.getId();
    }
}
