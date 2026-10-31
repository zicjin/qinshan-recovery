package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEquiptAcqScriptDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.mapper.EmcEquiptAcqScriptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAcqScriptDaoImpl extends MybatisDaoSupport<EmcEquiptAcqScriptBean, Long> implements EmcEquiptAcqScriptDao {

    @Autowired
    private EmcEquiptAcqScriptMapper EmcEquiptAcqScriptMapper;

    @Override
    public EmcEquiptAcqScriptMapper getMapper() {
        return EmcEquiptAcqScriptMapper;
    }

}
