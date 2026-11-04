/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqParamDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.mapper.CoreFileAcqParamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqParamDaoImpl
extends MybatisDaoSupport<CoreFileAcqParamBean, Long>
implements CoreFileAcqParamDao {
    @Autowired
    private CoreFileAcqParamMapper coreFileAcqParamMapper;

    public CoreFileAcqParamMapper getMapper() {
        return this.coreFileAcqParamMapper;
    }
}
