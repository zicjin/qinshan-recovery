/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageVersionDao;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.mapper.CoreFileManageVersionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileManageVersionDaoImpl
extends MybatisDaoSupport<CoreFileManageVersionBean, Long>
implements CoreFileManageVersionDao {
    @Autowired
    private CoreFileManageVersionMapper coreFileManageVersionMapper;

    public CoreFileManageVersionMapper getMapper() {
        return this.coreFileManageVersionMapper;
    }
}
