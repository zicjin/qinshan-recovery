package com.sunwayworld.emc.module.item.mtlpercheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.mtlpercheck.bean.EmcMtlPerCheckBean;
import com.sunwayworld.emc.module.item.mtlpercheck.dao.EmcMtlPerCheckDao;
import com.sunwayworld.emc.module.item.mtlpercheck.mapper.EmcMtlPerCheckMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcMtlPerCheckDaoImpl extends MybatisDaoSupport<EmcMtlPerCheckBean, Long> implements EmcMtlPerCheckDao {

    @Autowired
    private EmcMtlPerCheckMapper emcMtlPerCheckMapper;

    @Override
    public EmcMtlPerCheckMapper getMapper() {
        return emcMtlPerCheckMapper;
    }

}
