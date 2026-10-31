package com.sunwayworld.emc.module.mdm.equipt.filescript.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.filescript.bean.EmcEquiptFileScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.filescript.dao.EmcEquiptFileScriptDao;
import com.sunwayworld.emc.module.mdm.equipt.filescript.mapper.EmcEquiptFileScriptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptFileScriptDaoImpl extends MybatisDaoSupport<EmcEquiptFileScriptBean, Long> implements EmcEquiptFileScriptDao {
    @Autowired
    private EmcEquiptFileScriptMapper equiptFileScriptMapper;

    @Override
    public EmcEquiptFileScriptMapper getMapper() {
        return equiptFileScriptMapper;
    }
}
