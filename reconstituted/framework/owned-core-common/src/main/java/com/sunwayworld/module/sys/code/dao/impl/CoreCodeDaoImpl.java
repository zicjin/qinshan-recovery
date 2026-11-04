/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.dao.CoreCodeDao;
import com.sunwayworld.module.sys.code.mapper.CoreCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreCodeDaoImpl
extends MybatisDaoSupport<CoreCodeBean, Long>
implements CoreCodeDao {
    @Autowired
    private CoreCodeMapper codeMapper;

    public CoreCodeMapper getMapper() {
        return this.codeMapper;
    }
}
