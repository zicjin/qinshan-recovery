/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageDao;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.mapper.CoreFileManageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileManageDaoImpl
extends MybatisDaoSupport<CoreFileManageBean, Long>
implements CoreFileManageDao {
    @Autowired
    private CoreFileManageMapper coreFileManageMapper;

    public CoreFileManageMapper getMapper() {
        return this.coreFileManageMapper;
    }
}
