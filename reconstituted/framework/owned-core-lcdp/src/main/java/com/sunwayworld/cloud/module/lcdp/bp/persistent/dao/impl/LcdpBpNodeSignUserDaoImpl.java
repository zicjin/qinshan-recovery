/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpNodeSignUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpNodeSignUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpNodeSignUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpNodeSignUserDaoImpl
extends MybatisDaoSupport<LcdpBpNodeSignUserBean, Long>
implements LcdpBpNodeSignUserDao {
    @Autowired
    private LcdpBpNodeSignUserMapper lcdpBpNodeSignUserMapper;

    public LcdpBpNodeSignUserMapper getMapper() {
        return this.lcdpBpNodeSignUserMapper;
    }
}
