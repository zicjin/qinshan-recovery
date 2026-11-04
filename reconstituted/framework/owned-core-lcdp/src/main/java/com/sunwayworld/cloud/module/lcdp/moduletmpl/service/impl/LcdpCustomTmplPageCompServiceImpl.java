/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpCustomTmplPageCompDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpCustomTmplPageCompService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCustomTmplPageCompServiceImpl
implements LcdpCustomTmplPageCompService {
    @Autowired
    private LcdpCustomTmplPageCompDao lcdpCustomTmplPageCompDao;

    @Override
    public LcdpCustomTmplPageCompDao getDao() {
        return this.lcdpCustomTmplPageCompDao;
    }
}
