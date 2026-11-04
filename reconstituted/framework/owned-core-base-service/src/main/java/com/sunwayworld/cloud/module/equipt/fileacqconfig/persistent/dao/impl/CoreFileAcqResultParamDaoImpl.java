/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqResultParamDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.mapper.CoreFileAcqResultParamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqResultParamDaoImpl
extends MybatisDaoSupport<CoreFileAcqResultParamBean, Long>
implements CoreFileAcqResultParamDao {
    @Autowired
    private CoreFileAcqResultParamMapper coreFileAcqResultParamMapper;

    public CoreFileAcqResultParamMapper getMapper() {
        return this.coreFileAcqResultParamMapper;
    }
}
