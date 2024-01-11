package com.sunwayworld.emc.module.item.quotation.detectionfactor.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.bean.EmcDetectionfactorBean;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.dao.EmcDetectionfactorDao;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.mapper.EmcDetectionfactorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcDetectionfactorDaoImpl extends MybatisDaoSupport<EmcDetectionfactorBean, Long> implements EmcDetectionfactorDao {

    @Autowired
    private EmcDetectionfactorMapper emcDetectionfactorMapper;

    @Override
    public EmcDetectionfactorMapper getMapper() {
        return emcDetectionfactorMapper;
    }

}
