package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptMaintainRepairDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptMaintainRepairMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptMaintainRepairDaoImpl extends MybatisDaoSupport<EmcEquiptMaintainRepairBean, Long> implements EmcEquiptMaintainRepairDao {

    @Autowired
    private EmcEquiptMaintainRepairMapper emcEquiptMaintainRepairMapper;

    @Override
    public EmcEquiptMaintainRepairMapper getMapper() {
        return emcEquiptMaintainRepairMapper;
    }

}
