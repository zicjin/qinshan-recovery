package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasParamDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.mapper.EmcEasParamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasParamDaoImpl extends MybatisDaoSupport<EmcEasParamBean, Long> implements EmcEasParamDao {

    @Autowired
    private EmcEasParamMapper EmcEasParamMapper;

    @Override
    public EmcEasParamMapper getMapper() {
        return EmcEasParamMapper;
    }

}
