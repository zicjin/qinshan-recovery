// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.dao.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaClientBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaClientDao;
import com.sunwayworld.emc.module.mdm.oa.mapper.EmcOaClientMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcOaClientDaoImpl
extends MybatisDaoSupport<EmcOaClientBean, Long>
implements EmcOaClientDao {
    @Autowired
    private EmcOaClientMapper emcOaClientMapper;

    public EmcOaClientMapper getMapper() {
        return this.emcOaClientMapper;
    }
}
