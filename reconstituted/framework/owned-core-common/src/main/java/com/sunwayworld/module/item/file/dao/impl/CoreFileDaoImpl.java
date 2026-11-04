/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.dao.CoreFileDao;
import com.sunwayworld.module.item.file.mapper.CoreFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFileDaoImpl
extends MybatisDaoSupport<CoreFileBean, Long>
implements CoreFileDao {
    @Autowired
    private CoreFileMapper fileMapper;

    public CoreFileMapper getMapper() {
        return this.fileMapper;
    }
}
