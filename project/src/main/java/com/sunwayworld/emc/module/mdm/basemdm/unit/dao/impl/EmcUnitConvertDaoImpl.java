package com.sunwayworld.emc.module.mdm.basemdm.unit.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.dao.EmcUnitConvertDao;
import com.sunwayworld.emc.module.mdm.basemdm.unit.mapper.EmcUnitConvertMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

@Repository
@GikamBean
public class EmcUnitConvertDaoImpl extends MybatisDaoSupport<EmcUnitConvertBean, Long> implements EmcUnitConvertDao {
    @Autowired
    private EmcUnitConvertMapper emcUnitConvertMapper;

    @Override
    public EmcUnitConvertMapper getMapper() {
        return emcUnitConvertMapper;
    }
}
