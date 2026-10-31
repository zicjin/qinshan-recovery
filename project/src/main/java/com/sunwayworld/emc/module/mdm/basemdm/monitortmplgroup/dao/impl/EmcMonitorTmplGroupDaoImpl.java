package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.EmcMonitorTmplGroupDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.mapper.EmcMonitorTmplGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */
@Repository
public class EmcMonitorTmplGroupDaoImpl extends MybatisDaoSupport<EmcMonitorTmplGroupBean, Long> implements EmcMonitorTmplGroupDao {

    @Autowired
    private EmcMonitorTmplGroupMapper emcMonitorTmplGroupMapper;

    @Override
    public EmcMonitorTmplGroupMapper getMapper() {
        return emcMonitorTmplGroupMapper;
    }

}
