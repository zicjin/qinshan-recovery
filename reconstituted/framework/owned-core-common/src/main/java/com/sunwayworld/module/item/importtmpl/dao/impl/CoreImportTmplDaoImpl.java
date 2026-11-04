/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplBean;
import com.sunwayworld.module.item.importtmpl.dao.CoreImportTmplDao;
import com.sunwayworld.module.item.importtmpl.mapper.CoreImportTmplMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImportTmplDaoImpl
extends MybatisDaoSupport<CoreImportTmplBean, Long>
implements CoreImportTmplDao {
    @Autowired
    private CoreImportTmplMapper coreImportTmplMapper;

    public CoreImportTmplMapper getMapper() {
        return this.coreImportTmplMapper;
    }
}
