/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageReadRecordBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageReadRecordDao;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.mapper.CoreFileManageReadRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileManageReadRecordDaoImpl
extends MybatisDaoSupport<CoreFileManageReadRecordBean, Long>
implements CoreFileManageReadRecordDao {
    @Autowired
    private CoreFileManageReadRecordMapper coreFileManageReadRecordMapper;

    public CoreFileManageReadRecordMapper getMapper() {
        return this.coreFileManageReadRecordMapper;
    }
}
