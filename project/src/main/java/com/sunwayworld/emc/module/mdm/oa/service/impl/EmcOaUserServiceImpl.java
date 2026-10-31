// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.service.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaUserBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaUserDao;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaUserService;
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
public class EmcOaUserServiceImpl
implements EmcOaUserService {
    @Autowired
    private EmcOaUserDao emcOaUserDao;

    @Override
    public EmcOaUserDao getDao() {
        return this.emcOaUserDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOaUserBean emcOaUser = jsonWrapper.parseUnique(EmcOaUserBean.class);
        emcOaUser.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcOaUser);
        return emcOaUser.getId();
    }
}
