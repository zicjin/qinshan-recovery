package com.sunwayworld.emc.module.mdm.emp.emp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpQualDao;
import com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpQualMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEmpQualDaoImpl extends MybatisDaoSupport<EmcEmpQualBean, Long> implements EmcEmpQualDao {

    @Autowired
    private EmcEmpQualMapper emcEmpQualMapper;

    @Override
    public EmcEmpQualMapper getMapper() {
        return emcEmpQualMapper;
    }
}
