package com.sunwayworld.emc.module.mdm.emp.emp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpDao;
import com.sunwayworld.emc.module.mdm.emp.emp.mapper.EmcEmpMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEmpDaoImpl extends MybatisDaoSupport<EmcEmpBean, String> implements EmcEmpDao {
    @Autowired
    private EmcEmpMapper emcEmpMapper;

    @Override
    public EmcEmpMapper getMapper() {
        return emcEmpMapper;
    }

}
