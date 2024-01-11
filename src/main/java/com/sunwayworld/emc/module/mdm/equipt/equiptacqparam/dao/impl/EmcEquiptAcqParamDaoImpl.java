package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.dao.EmcEquiptAcqParamDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.mapper.EmcEquiptAcqParamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAcqParamDaoImpl extends MybatisDaoSupport<EmcEquiptAcqParamBean, Long> implements EmcEquiptAcqParamDao {

    @Autowired
    private EmcEquiptAcqParamMapper emcEquiptAcqParamMapper;

    @Override
    public EmcEquiptAcqParamMapper getMapper() {
        return emcEquiptAcqParamMapper;
    }

}
