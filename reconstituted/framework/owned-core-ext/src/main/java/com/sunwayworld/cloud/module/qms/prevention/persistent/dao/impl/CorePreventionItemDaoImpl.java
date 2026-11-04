/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionItemBean;
import com.sunwayworld.cloud.module.qms.prevention.persistent.dao.CorePreventionItemDao;
import com.sunwayworld.cloud.module.qms.prevention.persistent.mapper.CorePreventionItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CorePreventionItemDaoImpl
extends MybatisDaoSupport<CorePreventionItemBean, Long>
implements CorePreventionItemDao {
    @Autowired
    private CorePreventionItemMapper corePreventionItemMapper;

    public CorePreventionItemMapper getMapper() {
        return this.corePreventionItemMapper;
    }
}
