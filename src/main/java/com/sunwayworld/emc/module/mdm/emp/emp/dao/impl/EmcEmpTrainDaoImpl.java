package com.sunwayworld.emc.module.mdm.emp.emp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpTrainDao;
import com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpTrainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEmpTrainDaoImpl extends MybatisDaoSupport<EmcEmpTrainBean, Long> implements EmcEmpTrainDao {

    @Autowired
    private EmcEmpTrainMapper emcEmpTrainMapper;

    @Override
    public EmcEmpTrainMapper getMapper() {
        return emcEmpTrainMapper;
    }

}
