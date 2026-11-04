/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.dao.CoreHelpDocumentDao;
import com.sunwayworld.module.item.helpdocument.service.CoreHelpDocumentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreHelpDocumentServiceImpl
implements CoreHelpDocumentService {
    @Autowired
    private CoreHelpDocumentDao coreHelpDocumentDao;

    @Override
    public CoreHelpDocumentDao getDao() {
        return this.coreHelpDocumentDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        List<CoreHelpDocumentBean> coreHelpDocumentList = wrapper.parse(CoreHelpDocumentBean.class);
        coreHelpDocumentList.forEach(e -> {
            e.setDownload("0");
            e.setActivatedFlag("0");
        });
        this.getDao().insert(coreHelpDocumentList);
        return coreHelpDocumentList.get(0).getId();
    }
}
