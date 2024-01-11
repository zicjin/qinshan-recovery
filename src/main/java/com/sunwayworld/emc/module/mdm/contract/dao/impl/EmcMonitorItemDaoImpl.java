package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcMonitorItemDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcMonitorItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcMonitorItemDaoImpl extends MybatisDaoSupport<EmcMonitorItemBean, Long> implements EmcMonitorItemDao {

    @Autowired
    private EmcMonitorItemMapper emcMonitorItemMapper;

    @Override
    public EmcMonitorItemMapper getMapper() {
        return emcMonitorItemMapper;
    }

}
