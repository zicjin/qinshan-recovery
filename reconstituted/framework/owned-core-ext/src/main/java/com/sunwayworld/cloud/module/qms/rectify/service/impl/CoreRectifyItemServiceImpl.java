/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.service.impl;

import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyItemBean;
import com.sunwayworld.cloud.module.qms.rectify.persistent.dao.CoreRectifyItemDao;
import com.sunwayworld.cloud.module.qms.rectify.service.CoreRectifyItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRectifyItemServiceImpl
implements CoreRectifyItemService {
    @Autowired
    private CoreRectifyItemDao coreRectifyItemDao;

    @Override
    public CoreRectifyItemDao getDao() {
        return this.coreRectifyItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreRectifyItemBean coreRectifyItem = jsonWrapper.parseUnique(CoreRectifyItemBean.class);
        coreRectifyItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreRectifyItem);
        return coreRectifyItem.getId();
    }
}
