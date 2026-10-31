package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.EmcOrderDescDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.mapper.EmcOrderDescMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */
@Repository
public class EmcOrderDescDaoImpl extends MybatisDaoSupport<EmcOrderDescBean, Long> implements EmcOrderDescDao {

    @Autowired
    private EmcOrderDescMapper emcOrderDescMapper;

    @Override
    public EmcOrderDescMapper getMapper() {
        return emcOrderDescMapper;
    }

}
