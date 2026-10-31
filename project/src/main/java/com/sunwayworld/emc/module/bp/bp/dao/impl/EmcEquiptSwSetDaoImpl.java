// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSwSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptSwSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcEquiptSwSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcEquiptSwSetDaoImpl
extends MybatisDaoSupport<EmcEquiptSwSetBean, Long>
implements EmcEquiptSwSetDao {
    @Autowired
    private EmcEquiptSwSetMapper emcEquiptSwSetMapper;

    public EmcEquiptSwSetMapper getMapper() {
        return this.emcEquiptSwSetMapper;
    }
}
