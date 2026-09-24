// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptTempSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptTempSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcEquiptTempSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcEquiptTempSetDaoImpl
extends MybatisDaoSupport<EmcEquiptTempSetBean, Long>
implements EmcEquiptTempSetDao {
    @Autowired
    private EmcEquiptTempSetMapper emcEquiptTempSetMapper;

    public EmcEquiptTempSetMapper getMapper() {
        return this.emcEquiptTempSetMapper;
    }
}
