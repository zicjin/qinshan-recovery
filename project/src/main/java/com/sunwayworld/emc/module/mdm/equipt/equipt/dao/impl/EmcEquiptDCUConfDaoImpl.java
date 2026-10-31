package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDCUConfDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptDCUConfMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptDCUConfDaoImpl extends MybatisDaoSupport<EmcEquiptDCUConfBean, Long> implements EmcEquiptDCUConfDao {

    @Autowired
    private EmcEquiptDCUConfMapper emcEquiptDCUConfMapper;

    @Override
    public EmcEquiptDCUConfMapper getMapper() {
        return emcEquiptDCUConfMapper;
    }

}
