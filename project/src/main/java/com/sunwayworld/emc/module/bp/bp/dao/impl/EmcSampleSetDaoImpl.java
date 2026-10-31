// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcSampleSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcSampleSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcSampleSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcSampleSetDaoImpl
extends MybatisDaoSupport<EmcSampleSetBean, Long>
implements EmcSampleSetDao {
    @Autowired
    private EmcSampleSetMapper emcSampleSetMapper;

    public EmcSampleSetMapper getMapper() {
        return this.emcSampleSetMapper;
    }
}
