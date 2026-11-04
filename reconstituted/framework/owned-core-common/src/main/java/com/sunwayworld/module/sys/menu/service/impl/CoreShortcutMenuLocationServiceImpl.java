/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuLocationBean;
import com.sunwayworld.module.sys.menu.dao.CoreShortcutMenuLocationDao;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreShortcutMenuLocationServiceImpl
implements CoreShortcutMenuLocationService {
    @Autowired
    private CoreShortcutMenuLocationDao coreShortcutMenuLocationDao;

    @Override
    public CoreShortcutMenuLocationDao getDao() {
        return this.coreShortcutMenuLocationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void modifyLocation(Long location) {
        String loginUserId = LocalContextHelper.getLoginUserId();
        CoreShortcutMenuLocationBean coreShortcutMenuLocationBean = (CoreShortcutMenuLocationBean)this.getDao().selectByIdIfPresent(loginUserId);
        if (coreShortcutMenuLocationBean != null) {
            coreShortcutMenuLocationBean.setLocation(location);
            this.getDao().update(coreShortcutMenuLocationBean, new String[]{"LOCATION"});
        } else {
            coreShortcutMenuLocationBean = new CoreShortcutMenuLocationBean();
            coreShortcutMenuLocationBean.setId(loginUserId);
            coreShortcutMenuLocationBean.setLocation(1L);
            this.getDao().insert(coreShortcutMenuLocationBean);
        }
    }
}
