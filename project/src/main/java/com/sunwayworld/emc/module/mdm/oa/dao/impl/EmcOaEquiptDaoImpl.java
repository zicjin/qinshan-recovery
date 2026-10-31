// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.dao.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaEquiptBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaEquiptDao;
import com.sunwayworld.emc.module.mdm.oa.mapper.EmcOaEquiptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcOaEquiptDaoImpl
extends MybatisDaoSupport<EmcOaEquiptBean, Long>
implements EmcOaEquiptDao {
    @Autowired
    private EmcOaEquiptMapper emcOaEquiptMapper;

    public EmcOaEquiptMapper getMapper() {
        return this.emcOaEquiptMapper;
    }
}
