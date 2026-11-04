/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqPreprocessDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.mapper.CoreFileAcqPreprocessMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqPreprocessDaoImpl
extends MybatisDaoSupport<CoreFileAcqPreprocessBean, Long>
implements CoreFileAcqPreprocessDao {
    @Autowired
    private CoreFileAcqPreprocessMapper coreFileAcqPreprocessMapper;

    public CoreFileAcqPreprocessMapper getMapper() {
        return this.coreFileAcqPreprocessMapper;
    }
}
