// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptTempSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptTempSetDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptTempSetService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcEquiptTempSetServiceImpl
implements EmcEquiptTempSetService {
    @Autowired
    private EmcEquiptTempSetDao emcEquiptTempSetDao;

    @Override
    public EmcEquiptTempSetDao getDao() {
        return this.emcEquiptTempSetDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptTempSetBean emcEquiptTempSet = jsonWrapper.parseUnique(EmcEquiptTempSetBean.class);
        emcEquiptTempSet.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcEquiptTempSet);
        return emcEquiptTempSet.getId();
    }
}
