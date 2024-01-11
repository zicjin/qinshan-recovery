package com.sunwayworld.emc.module.mdm.basemdm.script.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.script.bean.EmcScriptBean;
import com.sunwayworld.emc.module.mdm.basemdm.script.dao.EmcScriptDao;
import com.sunwayworld.emc.module.mdm.basemdm.script.mapper.EmcScriptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcScriptDaoImpl extends MybatisDaoSupport<EmcScriptBean, String>implements EmcScriptDao {

    @Autowired
    private EmcScriptMapper emcScriptMapper;

    @Override
    public EmcScriptMapper getMapper() {
        return emcScriptMapper;
    }

}
