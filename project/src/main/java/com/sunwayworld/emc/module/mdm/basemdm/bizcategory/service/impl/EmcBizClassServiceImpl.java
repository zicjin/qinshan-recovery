// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.impl;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizClassBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizClassDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizClassService;
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
public class EmcBizClassServiceImpl
implements EmcBizClassService {
    @Autowired
    private EmcBizClassDao emcBizClassDao;

    @Override
    public EmcBizClassDao getDao() {
        return this.emcBizClassDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcBizClassBean emcBizClass = jsonWrapper.parseUnique(EmcBizClassBean.class);
        emcBizClass.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcBizClass);
        return emcBizClass.getId();
    }
}
