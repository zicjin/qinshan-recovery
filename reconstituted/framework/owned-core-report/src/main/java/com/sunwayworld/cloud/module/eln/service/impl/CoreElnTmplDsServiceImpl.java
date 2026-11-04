/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplDsDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplDsService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreElnTmplDsServiceImpl
implements CoreElnTmplDsService {
    @Autowired
    private CoreElnTmplDsDao coreElnTmplDsDao;

    @Override
    public CoreElnTmplDsDao getDao() {
        return this.coreElnTmplDsDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        List<CoreElnTmplDsBean> tmplDsList = wrapper.parse(CoreElnTmplDsBean.class);
        tmplDsList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(tmplDsList);
        return null;
    }
}
