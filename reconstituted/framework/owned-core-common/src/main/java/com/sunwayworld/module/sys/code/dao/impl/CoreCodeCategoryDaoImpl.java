/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.dao.CoreCodeCategoryDao;
import com.sunwayworld.module.sys.code.mapper.CoreCodeCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreCodeCategoryDaoImpl
extends MybatisDaoSupport<CoreCodeCategoryBean, String>
implements CoreCodeCategoryDao {
    @Autowired
    private CoreCodeCategoryMapper codeCategoryMapper;

    public CoreCodeCategoryMapper getMapper() {
        return this.codeCategoryMapper;
    }
}
