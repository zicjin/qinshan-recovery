/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.acqscript.bean.CoreEquiptAcqScriptBean;
import com.sunwayworld.cloud.module.equipt.acqscript.persistent.dao.CoreEquiptAcqScriptDao;
import com.sunwayworld.cloud.module.equipt.acqscript.persistent.mapper.CoreEquiptAcqScriptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptAcqScriptDaoImpl
extends MybatisDaoSupport<CoreEquiptAcqScriptBean, String>
implements CoreEquiptAcqScriptDao {
    @Autowired
    private CoreEquiptAcqScriptMapper coreEquiptAcqScriptMapper;

    public CoreEquiptAcqScriptMapper getMapper() {
        return this.coreEquiptAcqScriptMapper;
    }
}
