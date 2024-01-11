package com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcTestCapacityDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.mapper.EmcTestCapacityMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcTestCapacityDaoImpl extends MybatisDaoSupport<EmcTestCapacityBean, Long> implements EmcTestCapacityDao {

    @Autowired
    private EmcTestCapacityMapper emcTestCapacityMapper;

    @Override
    public EmcTestCapacityMapper getMapper() {
        return emcTestCapacityMapper;
    }

}
