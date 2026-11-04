/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqResultDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.mapper.CoreFileAcqResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqResultDaoImpl
extends MybatisDaoSupport<CoreFileAcqResultBean, Long>
implements CoreFileAcqResultDao {
    @Autowired
    private CoreFileAcqResultMapper coreFileAcqResultMapper;

    public CoreFileAcqResultMapper getMapper() {
        return this.coreFileAcqResultMapper;
    }
}
