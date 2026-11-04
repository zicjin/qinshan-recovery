/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.record.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.record.bean.CoreEquiptAcqRecordBean;
import com.sunwayworld.cloud.module.equipt.record.persistent.dao.CoreEquiptAcqRecordDao;
import com.sunwayworld.cloud.module.equipt.record.persistent.mapper.CoreEquiptAcqRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptAcqRecordDaoImpl
extends MybatisDaoSupport<CoreEquiptAcqRecordBean, Long>
implements CoreEquiptAcqRecordDao {
    @Autowired
    private CoreEquiptAcqRecordMapper coreEquiptAcqRecordMapper;

    public CoreEquiptAcqRecordMapper getMapper() {
        return this.coreEquiptAcqRecordMapper;
    }
}
