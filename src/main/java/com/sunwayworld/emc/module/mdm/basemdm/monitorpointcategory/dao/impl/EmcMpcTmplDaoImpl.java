package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.EmcMpcTmplDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.mapper.EmcMpcTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */
@Repository
public class EmcMpcTmplDaoImpl extends MybatisDaoSupport<EmcMpcTmplBean, Long> implements EmcMpcTmplDao {

    @Autowired
    private EmcMpcTmplMapper emcMpcTmplMapper;

    @Override
    public EmcMpcTmplMapper getMapper() {
        return emcMpcTmplMapper;
    }

}
