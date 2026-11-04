/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.addomain.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.addomain.bean.CoreAdDomainUserBean;
import com.sunwayworld.module.sys.addomain.dao.CoreAdDomainUserDao;
import com.sunwayworld.module.sys.addomain.mapper.CoreAdDomainUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAdDomainUserDaoImpl
extends MybatisDaoSupport<CoreAdDomainUserBean, String>
implements CoreAdDomainUserDao {
    @Autowired
    private CoreAdDomainUserMapper coreAdDomainUserMapper;

    public CoreAdDomainUserMapper getMapper() {
        return this.coreAdDomainUserMapper;
    }
}
