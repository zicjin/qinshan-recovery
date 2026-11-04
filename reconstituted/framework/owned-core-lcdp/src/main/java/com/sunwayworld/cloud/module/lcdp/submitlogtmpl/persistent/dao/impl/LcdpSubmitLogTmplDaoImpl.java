/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlogtmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.bean.LcdpSubmitLogTmplBean;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.persistent.dao.LcdpSubmitLogTmplDao;
import com.sunwayworld.cloud.module.lcdp.submitlogtmpl.persistent.mapper.LcdpSubmitLogTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpSubmitLogTmplDaoImpl
extends MybatisDaoSupport<LcdpSubmitLogTmplBean, Long>
implements LcdpSubmitLogTmplDao {
    @Autowired
    private LcdpSubmitLogTmplMapper lcdpSubmitLogTmplMapper;

    public LcdpSubmitLogTmplMapper getMapper() {
        return this.lcdpSubmitLogTmplMapper;
    }
}
