/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.importtmpl.dao.CoreImportTmplConfigDao;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImportTmplConfigServiceImpl
implements CoreImportTmplConfigService {
    @Autowired
    private CoreImportTmplConfigDao coreImportTmplConfigDao;

    @Override
    public CoreImportTmplConfigDao getDao() {
        return this.coreImportTmplConfigDao;
    }
}
