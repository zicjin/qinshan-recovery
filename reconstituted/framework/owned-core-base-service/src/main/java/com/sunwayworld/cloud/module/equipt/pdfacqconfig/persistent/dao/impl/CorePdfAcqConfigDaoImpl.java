/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.mapper.CorePdfAcqConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePdfAcqConfigDaoImpl
extends MybatisDaoSupport<CorePdfAcqConfigBean, Long>
implements CorePdfAcqConfigDao {
    @Autowired
    private CorePdfAcqConfigMapper pdfAcqConfigMapper;

    public CorePdfAcqConfigMapper getMapper() {
        return this.pdfAcqConfigMapper;
    }
}
