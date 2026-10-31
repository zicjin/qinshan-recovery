// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.service.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaClientBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaClientDao;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaClientService;
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
public class EmcOaClientServiceImpl
implements EmcOaClientService {
    @Autowired
    private EmcOaClientDao emcOaClientDao;

    @Override
    public EmcOaClientDao getDao() {
        return this.emcOaClientDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOaClientBean emcOaClient = jsonWrapper.parseUnique(EmcOaClientBean.class);
        emcOaClient.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcOaClient);
        return emcOaClient.getId();
    }
}
