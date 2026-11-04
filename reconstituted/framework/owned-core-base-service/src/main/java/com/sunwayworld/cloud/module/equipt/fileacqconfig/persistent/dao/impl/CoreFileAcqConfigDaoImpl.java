/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.mapper.CoreFileAcqConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqConfigDaoImpl
extends MybatisDaoSupport<CoreFileAcqConfigBean, Long>
implements CoreFileAcqConfigDao {
    @Autowired
    private CoreFileAcqConfigMapper coreFileAcqConfigMapper;

    public CoreFileAcqConfigMapper getMapper() {
        return this.coreFileAcqConfigMapper;
    }
}
