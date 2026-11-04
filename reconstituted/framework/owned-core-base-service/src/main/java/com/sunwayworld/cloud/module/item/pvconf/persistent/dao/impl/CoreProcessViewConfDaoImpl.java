/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewConfDao;
import com.sunwayworld.cloud.module.item.pvconf.persistent.mapper.CoreProcessViewConfMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreProcessViewConfDaoImpl
extends MybatisDaoSupport<CoreProcessViewConfBean, Long>
implements CoreProcessViewConfDao {
    @Autowired
    private CoreProcessViewConfMapper processViewConfMapper;

    public CoreProcessViewConfMapper getMapper() {
        return this.processViewConfMapper;
    }
}
