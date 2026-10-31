// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.dao.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaOrgBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaOrgDao;
import com.sunwayworld.emc.module.mdm.oa.mapper.EmcOaOrgMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcOaOrgDaoImpl
extends MybatisDaoSupport<EmcOaOrgBean, Long>
implements EmcOaOrgDao {
    @Autowired
    private EmcOaOrgMapper emcOaOrgMapper;

    public EmcOaOrgMapper getMapper() {
        return this.emcOaOrgMapper;
    }
}
