/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManagePrintRecorBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManagePrintRecorDao;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.mapper.CoreFileManagePrintRecorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileManagePrintRecorDaoImpl
extends MybatisDaoSupport<CoreFileManagePrintRecorBean, Long>
implements CoreFileManagePrintRecorDao {
    @Autowired
    private CoreFileManagePrintRecorMapper coreFileManagePrintRecorMapper;

    public CoreFileManagePrintRecorMapper getMapper() {
        return this.coreFileManagePrintRecorMapper;
    }
}
