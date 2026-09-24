// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcEquiptSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcEquiptSetDaoImpl
extends MybatisDaoSupport<EmcEquiptSetBean, Long>
implements EmcEquiptSetDao {
    @Autowired
    private EmcEquiptSetMapper emcEquiptSetMapper;

    public EmcEquiptSetMapper getMapper() {
        return this.emcEquiptSetMapper;
    }
}
