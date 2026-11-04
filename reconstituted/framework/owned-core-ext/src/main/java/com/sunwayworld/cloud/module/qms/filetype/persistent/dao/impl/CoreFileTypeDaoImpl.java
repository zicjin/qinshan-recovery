/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileTypeBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileTypeDao;
import com.sunwayworld.cloud.module.qms.filetype.persistent.mapper.CoreFileTypeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileTypeDaoImpl
extends MybatisDaoSupport<CoreFileTypeBean, Long>
implements CoreFileTypeDao {
    @Autowired
    private CoreFileTypeMapper coreFileTypeMapper;

    public CoreFileTypeMapper getMapper() {
        return this.coreFileTypeMapper;
    }
}
