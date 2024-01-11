package com.sunwayworld.emc.module.mdm.basemdm.unit.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.dao.EmcUnitDao;
import com.sunwayworld.emc.module.mdm.basemdm.unit.mapper.EmcUnitMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

@Repository
public class EmcUnitDaoImpl extends MybatisDaoSupport<EmcUnitBean, String> implements EmcUnitDao {
    @Autowired
    private EmcUnitMapper emcUnitMapper;

    @Override
    public EmcUnitMapper getMapper() {
        return emcUnitMapper;
    }
}
