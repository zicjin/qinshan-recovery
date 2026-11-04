/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewSubConfDao;
import com.sunwayworld.cloud.module.item.pvconf.persistent.mapper.CoreProcessViewSubConfMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreProcessViewSubConfDaoImpl
extends MybatisDaoSupport<CoreProcessViewSubConfBean, Long>
implements CoreProcessViewSubConfDao {
    @Autowired
    private CoreProcessViewSubConfMapper processViewSubConfMapper;

    public CoreProcessViewSubConfMapper getMapper() {
        return this.processViewSubConfMapper;
    }
}
