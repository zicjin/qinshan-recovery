package com.sunwayworld.emc.module.item.staffability.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.staffability.bean.EmcStaffAbilityBean;
import com.sunwayworld.emc.module.item.staffability.dao.EmcStaffAbilityDao;
import com.sunwayworld.emc.module.item.staffability.mapper.EmcStaffAbilityMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcStaffAbilityDaoImpl extends MybatisDaoSupport<EmcStaffAbilityBean, Long> implements EmcStaffAbilityDao {

    @Autowired
    private EmcStaffAbilityMapper emcStaffAbilityMapper;

    @Override
    public EmcStaffAbilityMapper getMapper() {
        return emcStaffAbilityMapper;
    }

}
