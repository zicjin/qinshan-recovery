// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptFlowSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptFlowSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcEquiptFlowSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcEquiptFlowSetDaoImpl
extends MybatisDaoSupport<EmcEquiptFlowSetBean, Long>
implements EmcEquiptFlowSetDao {
    @Autowired
    private EmcEquiptFlowSetMapper emcEquiptFlowSetMapper;

    public EmcEquiptFlowSetMapper getMapper() {
        return this.emcEquiptFlowSetMapper;
    }
}
