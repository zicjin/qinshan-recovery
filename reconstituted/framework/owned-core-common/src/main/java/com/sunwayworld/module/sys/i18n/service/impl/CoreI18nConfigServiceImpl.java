/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nConfigDao;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreI18nConfigServiceImpl
implements CoreI18nConfigService {
    @Autowired
    private CoreI18nConfigDao dao;

    @Override
    public CoreI18nConfigDao getDao() {
        return this.dao;
    }

    @Override
    public List<CoreI18nConfigBean> selectAll() {
        return CoreI18nConfigService.super.selectAll();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        List newI18nConfigList = wrapper.parse(this.getDao().getType());
        List<CoreI18nConfigBean> oldI18nConfigList = this.selectAll();
        this.getDao().deleteBy(oldI18nConfigList, new String[0]);
        this.getDao().insert(newI18nConfigList);
        return ((CoreI18nConfigBean)newI18nConfigList.get(0)).getId();
    }
}
