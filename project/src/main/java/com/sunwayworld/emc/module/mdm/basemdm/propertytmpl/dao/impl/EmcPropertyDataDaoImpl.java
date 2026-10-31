package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyDataDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.mapper.EmcPropertyDataMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcPropertyDataDaoImpl extends MybatisDaoSupport<EmcPropertyDataBean, Long> implements EmcPropertyDataDao {

    @Autowired
    private EmcPropertyDataMapper emcPropertyDataMapper;

    @Override
    public EmcPropertyDataMapper getMapper() {
        return emcPropertyDataMapper;
    }

}
