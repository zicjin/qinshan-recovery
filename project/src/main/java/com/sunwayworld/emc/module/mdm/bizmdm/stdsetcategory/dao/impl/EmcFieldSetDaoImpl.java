package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcFieldSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcFieldSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.mapper.EmcFieldSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcFieldSetDaoImpl extends MybatisDaoSupport<EmcFieldSetBean, Long>implements EmcFieldSetDao {

    @Autowired
    private EmcFieldSetMapper emcFieldSetMapper;

    @Override
    public EmcFieldSetMapper getMapper() {
        return emcFieldSetMapper;
    }

}
