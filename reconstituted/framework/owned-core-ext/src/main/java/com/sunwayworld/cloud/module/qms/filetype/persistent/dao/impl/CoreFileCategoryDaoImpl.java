/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileCategoryDao;
import com.sunwayworld.cloud.module.qms.filetype.persistent.mapper.CoreFileCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileCategoryDaoImpl
extends MybatisDaoSupport<CoreFileCategoryBean, Long>
implements CoreFileCategoryDao {
    @Autowired
    private CoreFileCategoryMapper coreFileCategoryMapper;

    public CoreFileCategoryMapper getMapper() {
        return this.coreFileCategoryMapper;
    }
}
