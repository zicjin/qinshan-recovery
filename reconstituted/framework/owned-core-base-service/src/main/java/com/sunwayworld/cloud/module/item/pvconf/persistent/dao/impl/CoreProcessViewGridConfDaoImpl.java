/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewGridConfDao;
import com.sunwayworld.cloud.module.item.pvconf.persistent.mapper.CoreProcessViewGridConfMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreProcessViewGridConfDaoImpl
extends MybatisDaoSupport<CoreProcessViewGridConfBean, Long>
implements CoreProcessViewGridConfDao {
    @Autowired
    private CoreProcessViewGridConfMapper processViewGridConfMapper;

    public CoreProcessViewGridConfMapper getMapper() {
        return this.processViewGridConfMapper;
    }
}
