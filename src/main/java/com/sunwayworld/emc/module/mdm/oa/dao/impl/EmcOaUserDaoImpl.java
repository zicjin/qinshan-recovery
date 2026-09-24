// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.dao.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaUserBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaUserDao;
import com.sunwayworld.emc.module.mdm.oa.mapper.EmcOaUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcOaUserDaoImpl
extends MybatisDaoSupport<EmcOaUserBean, Long>
implements EmcOaUserDao {
    @Autowired
    private EmcOaUserMapper emcOaUserMapper;

    public EmcOaUserMapper getMapper() {
        return this.emcOaUserMapper;
    }
}
