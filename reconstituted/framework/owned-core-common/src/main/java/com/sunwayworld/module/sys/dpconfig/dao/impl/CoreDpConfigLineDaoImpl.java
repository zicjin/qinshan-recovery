/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigLineBean;
import com.sunwayworld.module.sys.dpconfig.dao.CoreDpConfigLineDao;
import com.sunwayworld.module.sys.dpconfig.mapper.CoreDpConfigLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDpConfigLineDaoImpl
extends MybatisDaoSupport<CoreDpConfigLineBean, Long>
implements CoreDpConfigLineDao {
    @Autowired
    private CoreDpConfigLineMapper dpConfigLineMapper;

    public CoreDpConfigLineMapper getMapper() {
        return this.dpConfigLineMapper;
    }
}
