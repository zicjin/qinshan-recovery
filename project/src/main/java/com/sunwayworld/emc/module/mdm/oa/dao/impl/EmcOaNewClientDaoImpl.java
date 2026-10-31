// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.dao.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaNewClientBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaNewClientDao;
import com.sunwayworld.emc.module.mdm.oa.mapper.EmcOaNewClientMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcOaNewClientDaoImpl
extends MybatisDaoSupport<EmcOaNewClientBean, Long>
implements EmcOaNewClientDao {
    @Autowired
    private EmcOaNewClientMapper emcOaNewClientMapper;

    public EmcOaNewClientMapper getMapper() {
        return this.emcOaNewClientMapper;
    }
}
