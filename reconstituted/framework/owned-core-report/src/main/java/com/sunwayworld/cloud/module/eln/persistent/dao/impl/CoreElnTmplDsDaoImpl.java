/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplDsDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnTmplDsMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnTmplDsDaoImpl
extends MybatisDaoSupport<CoreElnTmplDsBean, Long>
implements CoreElnTmplDsDao {
    @Autowired
    private CoreElnTmplDsMapper coreElnTmplDsMapper;

    public CoreElnTmplDsMapper getMapper() {
        return this.coreElnTmplDsMapper;
    }
}
