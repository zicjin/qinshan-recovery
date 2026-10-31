package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestMethodDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptTestMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magt@sunwayworld.com
 * @date 2019-09-20
 */
@Repository
public class EmcEquiptTestMethodDaoImpl extends MybatisDaoSupport<EmcEquiptTestMethodBean, Long> implements EmcEquiptTestMethodDao {
    
    @Autowired
    private EmcEquiptTestMethodMapper emcEquiptTestMethodMapper;
    
    @Override
    public EmcEquiptTestMethodMapper getMapper() {
        return emcEquiptTestMethodMapper;
    }
    
}
