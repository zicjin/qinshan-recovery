package com.sunwayworld.emc.module.item.staffabilitycheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.staffabilitycheck.bean.EmcStaffAbalityCheckBean;
import com.sunwayworld.emc.module.item.staffabilitycheck.dao.EmcStaffAbalityCheckDao;
import com.sunwayworld.emc.module.item.staffabilitycheck.mapper.EmcStaffAbalityCheckMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcStaffAbalityCheckDaoImpl extends MybatisDaoSupport<EmcStaffAbalityCheckBean, Long> implements EmcStaffAbalityCheckDao {

    @Autowired
    private EmcStaffAbalityCheckMapper emcStaffAbalityCheckMapper;

    @Override
    public EmcStaffAbalityCheckMapper getMapper() {
        return emcStaffAbalityCheckMapper;
    }

}
