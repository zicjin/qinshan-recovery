// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.dao.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptStandardSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptStandardSetDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcEquiptStandardSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcEquiptStandardSetDaoImpl
extends MybatisDaoSupport<EmcEquiptStandardSetBean, Long>
implements EmcEquiptStandardSetDao {
    @Autowired
    private EmcEquiptStandardSetMapper emcEquiptStandardSetMapper;

    public EmcEquiptStandardSetMapper getMapper() {
        return this.emcEquiptStandardSetMapper;
    }
}
