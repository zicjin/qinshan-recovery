package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcStandardSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.mapper.EmcStandardSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcStandardSetDaoImpl extends MybatisDaoSupport<EmcStandardSetBean, Long>implements EmcStandardSetDao {

    @Autowired
    private EmcStandardSetMapper emcStandardSetMapper;

    @Override
    public EmcStandardSetMapper getMapper() {
        return emcStandardSetMapper;
    }

}
