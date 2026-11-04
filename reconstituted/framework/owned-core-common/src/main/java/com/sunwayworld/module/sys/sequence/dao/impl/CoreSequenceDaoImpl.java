/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.dao.CoreSequenceDao;
import com.sunwayworld.module.sys.sequence.mapper.CoreSequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreSequenceDaoImpl
extends MybatisDaoSupport<CoreSequenceBean, String>
implements CoreSequenceDao {
    @Autowired
    private CoreSequenceMapper sequenceMapper;

    public CoreSequenceMapper getMapper() {
        return this.sequenceMapper;
    }
}
