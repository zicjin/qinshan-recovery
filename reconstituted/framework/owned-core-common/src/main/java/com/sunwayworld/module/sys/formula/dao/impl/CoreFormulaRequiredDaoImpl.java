/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.formula.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.dao.CoreFormulaRequiredDao;
import com.sunwayworld.module.sys.formula.mapper.CoreFormulaRequiredMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFormulaRequiredDaoImpl
extends MybatisDaoSupport<CoreFormulaRequiredBean, Long>
implements CoreFormulaRequiredDao {
    @Autowired
    private CoreFormulaRequiredMapper mapper;

    public CoreFormulaRequiredMapper getMapper() {
        return this.mapper;
    }
}
