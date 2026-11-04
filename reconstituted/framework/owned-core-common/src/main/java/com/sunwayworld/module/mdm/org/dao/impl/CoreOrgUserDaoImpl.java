/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import com.sunwayworld.module.mdm.org.dao.CoreOrgUserDao;
import com.sunwayworld.module.mdm.org.mapper.CoreOrgUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreOrgUserDaoImpl
extends MybatisDaoSupport<CoreOrgUserBean, Long>
implements CoreOrgUserDao {
    @Autowired
    private CoreOrgUserMapper coreOrgUserMapper;

    public CoreOrgUserMapper getMapper() {
        return this.coreOrgUserMapper;
    }
}
