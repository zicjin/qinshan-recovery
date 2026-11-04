/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import com.sunwayworld.module.sys.sequence.dao.CoreSequenceLineDao;
import com.sunwayworld.module.sys.sequence.mapper.CoreSequenceLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSequenceLineDaoImpl
extends MybatisDaoSupport<CoreSequenceLineBean, Long>
implements CoreSequenceLineDao {
    @Autowired
    private CoreSequenceLineMapper CoreSequenceLineMapper;

    public CoreSequenceLineMapper getMapper() {
        return this.CoreSequenceLineMapper;
    }
}
