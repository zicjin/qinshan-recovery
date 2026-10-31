package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodDefaultQcDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestMethodDefaultQcMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年06月17日
*/
@Repository
public class EmcTestMethodDefaultQcDaoImpl extends MybatisDaoSupport<EmcTestMethodDefaultQcBean, Long> implements EmcTestMethodDefaultQcDao {

    @Autowired
    private EmcTestMethodDefaultQcMapper emcTestMethodDefaultQcMapper;

    @Override
    public EmcTestMethodDefaultQcMapper getMapper() {
        return emcTestMethodDefaultQcMapper;
    }
}
