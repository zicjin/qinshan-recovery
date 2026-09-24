// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.impl;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizClassBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizClassDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.mapper.EmcBizClassMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcBizClassDaoImpl
extends MybatisDaoSupport<EmcBizClassBean, Long>
implements EmcBizClassDao {
    @Autowired
    private EmcBizClassMapper emcBizClassMapper;

    public EmcBizClassMapper getMapper() {
        return this.emcBizClassMapper;
    }
}
