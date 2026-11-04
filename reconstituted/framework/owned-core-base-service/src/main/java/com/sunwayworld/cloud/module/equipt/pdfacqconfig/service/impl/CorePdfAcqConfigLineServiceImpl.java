/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqConfigLineDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqConfigLineService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CorePdfAcqConfigLineServiceImpl
implements CorePdfAcqConfigLineService {
    @Autowired
    private CorePdfAcqConfigLineDao pdfAcqConfigLineDao;

    @Override
    public CorePdfAcqConfigLineDao getDao() {
        return this.pdfAcqConfigLineDao;
    }
}
