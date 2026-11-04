/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplVersionDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnTmplVersionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnTmplVersionDaoImpl
extends MybatisDaoSupport<CoreElnTmplVersionBean, Long>
implements CoreElnTmplVersionDao {
    @Autowired
    private CoreElnTmplVersionMapper coreElnTmplVersionMapper;

    public CoreElnTmplVersionMapper getMapper() {
        return this.coreElnTmplVersionMapper;
    }
}
