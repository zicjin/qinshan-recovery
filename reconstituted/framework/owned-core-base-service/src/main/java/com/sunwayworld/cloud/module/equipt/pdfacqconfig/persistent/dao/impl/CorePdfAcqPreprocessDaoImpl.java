/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqPreprocessDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.mapper.CorePdfAcqPreprocessMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CorePdfAcqPreprocessDaoImpl
extends MybatisDaoSupport<CorePdfAcqPreprocessBean, Long>
implements CorePdfAcqPreprocessDao {
    @Autowired
    private CorePdfAcqPreprocessMapper corePdfAcqPreprocessMapper;

    public CorePdfAcqPreprocessMapper getMapper() {
        return this.corePdfAcqPreprocessMapper;
    }
}
