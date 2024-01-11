package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.dao.EmcEquiptRequisitionDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.mapper.EmcEquiptRequisitionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptRequisitionDaoImpl extends MybatisDaoSupport<EmcEquiptRequisitionBean, Long> implements EmcEquiptRequisitionDao {

    @Autowired
    private EmcEquiptRequisitionMapper emcEquiptRequisitionMapper;

    @Override
    public EmcEquiptRequisitionMapper getMapper() {
        return emcEquiptRequisitionMapper;
    }

}
