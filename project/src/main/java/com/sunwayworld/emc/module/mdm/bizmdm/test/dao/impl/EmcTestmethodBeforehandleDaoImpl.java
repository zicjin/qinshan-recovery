package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestmethodBeforehandleBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestmethodBeforehandleDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestmethodBeforehandleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcTestmethodBeforehandleDaoImpl extends MybatisDaoSupport<EmcTestmethodBeforehandleBean, Long> implements EmcTestmethodBeforehandleDao {

    @Autowired
    private EmcTestmethodBeforehandleMapper emcTestmethodBeforehandleMapper;

    @Override
    public EmcTestmethodBeforehandleMapper getMapper() {
        return emcTestmethodBeforehandleMapper;
    }

}
