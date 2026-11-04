/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewMasterConfDao;
import com.sunwayworld.cloud.module.item.pvconf.persistent.mapper.CoreProcessViewMasterConfMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreProcessViewMasterConfDaoImpl
extends MybatisDaoSupport<CoreProcessViewMasterConfBean, Long>
implements CoreProcessViewMasterConfDao {
    @Autowired
    private CoreProcessViewMasterConfMapper processViewMasterConfMapper;

    public CoreProcessViewMasterConfMapper getMapper() {
        return this.processViewMasterConfMapper;
    }
}
