/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinconfig.service.impl;

import com.sunwayworld.cloud.module.lcdp.checkinconfig.bean.LcdpCheckinConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.persistent.dao.LcdpCheckinConfigDao;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.service.LcdpCheckinConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpCheckinConfigServiceImpl
implements LcdpCheckinConfigService {
    @Autowired
    private LcdpCheckinConfigDao lcdpCheckinConfigDao;

    @Override
    public LcdpCheckinConfigDao getDao() {
        return this.lcdpCheckinConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpCheckinConfigBean checkinConfig = jsonWrapper.parseUnique(LcdpCheckinConfigBean.class);
        checkinConfig.setId(ApplicationContextHelper.getNextIdentity());
        checkinConfig.setLicense(StringUtils.randomUUID());
        this.getDao().insert(checkinConfig);
        return checkinConfig.getId();
    }
}
