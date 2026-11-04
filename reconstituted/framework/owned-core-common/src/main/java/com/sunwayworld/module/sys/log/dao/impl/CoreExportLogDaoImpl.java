/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.module.sys.log.bean.CoreExportLogBean;
import com.sunwayworld.module.sys.log.dao.CoreExportLogDao;
import com.sunwayworld.module.sys.log.mapper.CoreExportLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExportLogDaoImpl
extends MybatisDaoSupport<CoreExportLogBean, Long>
implements CoreExportLogDao {
    @Autowired
    private CoreExportLogMapper exportLogMapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return this.exportLogMapper;
    }
}
