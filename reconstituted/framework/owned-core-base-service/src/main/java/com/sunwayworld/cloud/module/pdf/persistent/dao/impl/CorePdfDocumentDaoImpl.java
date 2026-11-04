/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.persistent.dao.impl;

import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.persistent.dao.CorePdfDocumentDao;
import com.sunwayworld.cloud.module.pdf.persistent.mapper.CorePdfDocumentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePdfDocumentDaoImpl
extends MybatisDaoSupport<CorePdfDocumentBean, Long>
implements CorePdfDocumentDao {
    @Autowired
    private CorePdfDocumentMapper corePdfDocumentMapper;

    public CorePdfDocumentMapper getMapper() {
        return this.corePdfDocumentMapper;
    }
}
