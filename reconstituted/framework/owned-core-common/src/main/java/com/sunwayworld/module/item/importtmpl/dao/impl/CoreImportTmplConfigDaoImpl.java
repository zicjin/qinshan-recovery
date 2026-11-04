/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplConfigBean;
import com.sunwayworld.module.item.importtmpl.dao.CoreImportTmplConfigDao;
import com.sunwayworld.module.item.importtmpl.mapper.CoreImportTmplConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImportTmplConfigDaoImpl
extends MybatisDaoSupport<CoreImportTmplConfigBean, Long>
implements CoreImportTmplConfigDao {
    @Autowired
    private CoreImportTmplConfigMapper coreImportTmplConfigMapper;

    public CoreImportTmplConfigMapper getMapper() {
        return this.coreImportTmplConfigMapper;
    }
}
