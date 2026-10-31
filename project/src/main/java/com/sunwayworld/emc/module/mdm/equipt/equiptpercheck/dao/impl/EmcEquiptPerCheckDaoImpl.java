package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean.EmcEquiptPerCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.dao.EmcEquiptPerCheckDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.mapper.EmcEquiptPerCheckMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptPerCheckDaoImpl extends MybatisDaoSupport<EmcEquiptPerCheckBean, Long> implements EmcEquiptPerCheckDao {

    @Autowired
    private EmcEquiptPerCheckMapper emcEquiptPerCheckMapper;

    @Override
    public EmcEquiptPerCheckMapper getMapper() {
        return emcEquiptPerCheckMapper;
    }

}
