/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigLineBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqConfigLineDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.mapper.CorePdfAcqConfigLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePdfAcqConfigLineDaoImpl
extends MybatisDaoSupport<CorePdfAcqConfigLineBean, Long>
implements CorePdfAcqConfigLineDao {
    @Autowired
    private CorePdfAcqConfigLineMapper pdfAcqConfigLineMapper;

    public CorePdfAcqConfigLineMapper getMapper() {
        return this.pdfAcqConfigLineMapper;
    }
}
