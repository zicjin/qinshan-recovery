// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.dao.impl;

import com.sunwayworld.emc.module.item.admarea.bean.EmcAdmAreaBean;
import com.sunwayworld.emc.module.item.admarea.dao.EmcAdmAreaDao;
import com.sunwayworld.emc.module.item.admarea.mapper.EmcAdmAreaMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcAdmAreaDaoImpl
extends MybatisDaoSupport<EmcAdmAreaBean, String>
implements EmcAdmAreaDao {
    @Autowired
    private EmcAdmAreaMapper emcAdmAreaMapper;

    public EmcAdmAreaMapper getMapper() {
        return this.emcAdmAreaMapper;
    }
}
