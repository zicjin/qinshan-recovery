/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileCategoryRoleAuthDao;
import com.sunwayworld.cloud.module.qms.filetype.persistent.mapper.CoreFileCategoryRoleAuthMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileCategoryRoleAuthDaoImpl
extends MybatisDaoSupport<CoreFileCategoryRoleAuthBean, Long>
implements CoreFileCategoryRoleAuthDao {
    @Autowired
    private CoreFileCategoryRoleAuthMapper coreFileCategoryRoleAuthMapper;

    public CoreFileCategoryRoleAuthMapper getMapper() {
        return this.coreFileCategoryRoleAuthMapper;
    }
}
