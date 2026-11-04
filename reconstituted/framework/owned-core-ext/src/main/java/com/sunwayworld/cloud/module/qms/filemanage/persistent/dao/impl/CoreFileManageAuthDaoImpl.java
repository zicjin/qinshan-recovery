/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageAuthDao;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.mapper.CoreFileManageAuthMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileManageAuthDaoImpl
extends MybatisDaoSupport<CoreFileManageAuthBean, Long>
implements CoreFileManageAuthDao {
    @Autowired
    private CoreFileManageAuthMapper coreFileManageAuthMapper;

    public CoreFileManageAuthMapper getMapper() {
        return this.coreFileManageAuthMapper;
    }
}
