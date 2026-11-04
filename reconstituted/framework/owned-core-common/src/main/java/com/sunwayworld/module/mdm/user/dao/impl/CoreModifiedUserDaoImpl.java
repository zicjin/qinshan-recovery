/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserDao;
import com.sunwayworld.module.mdm.user.mapper.CoreModifiedUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreModifiedUserDaoImpl
extends MybatisDaoSupport<CoreModifiedUserBean, Long>
implements CoreModifiedUserDao {
    @Autowired
    private CoreModifiedUserMapper modifiedUserMapper;

    public CoreModifiedUserMapper getMapper() {
        return this.modifiedUserMapper;
    }
}
