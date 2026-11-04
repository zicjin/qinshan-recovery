/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutconfig.service.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutconfig.bean.LcdpCheckoutConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.persistent.dao.LcdpCheckoutConfigDao;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.service.LcdpCheckoutConfigService;
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
public class LcdpCheckoutConfigServiceImpl
implements LcdpCheckoutConfigService {
    @Autowired
    private LcdpCheckoutConfigDao lcdpCheckoutConfigDao;

    @Override
    public LcdpCheckoutConfigDao getDao() {
        return this.lcdpCheckoutConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpCheckoutConfigBean checkoutConfig = jsonWrapper.parseUnique(LcdpCheckoutConfigBean.class);
        checkoutConfig.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(checkoutConfig);
        return checkoutConfig.getId();
    }
}
