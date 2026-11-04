/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnAtRecordBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnAtRecordDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnAtRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnAtRecordDaoImpl
extends MybatisDaoSupport<CoreElnAtRecordBean, Long>
implements CoreElnAtRecordDao {
    @Autowired
    private CoreElnAtRecordMapper coreElnChangeRecordMapper;

    public CoreElnAtRecordMapper getMapper() {
        return this.coreElnChangeRecordMapper;
    }
}
