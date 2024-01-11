package com.sunwayworld.emc.module.mdm.emp.emp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpTestDao;
import com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpTestMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEmpTestDaoImpl extends MybatisDaoSupport<EmcEmpTestBean, Long> implements EmcEmpTestDao {

    @Autowired
    private EmcEmpTestMapper emcEmpTestMapper;

    @Override
    public EmcEmpTestMapper getMapper() {
        return emcEmpTestMapper;
    }
}
