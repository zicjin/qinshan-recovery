/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpBizSignUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpBizSignUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpBizSignUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpBizSignUserDaoImpl
extends MybatisDaoSupport<LcdpBpBizSignUserBean, Long>
implements LcdpBpBizSignUserDao {
    @Autowired
    private LcdpBpBizSignUserMapper lcdpBpBizSignUserMapper;

    public LcdpBpBizSignUserMapper getMapper() {
        return this.lcdpBpBizSignUserMapper;
    }
}
