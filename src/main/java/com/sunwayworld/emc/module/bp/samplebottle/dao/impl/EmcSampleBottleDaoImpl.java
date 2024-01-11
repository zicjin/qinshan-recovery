package com.sunwayworld.emc.module.bp.samplebottle.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.samplebottle.bean.EmcSampleBottleBean;
import com.sunwayworld.emc.module.bp.samplebottle.dao.EmcSampleBottleDao;
import com.sunwayworld.emc.module.bp.samplebottle.mapper.EmcSampleBottleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSampleBottleDaoImpl extends MybatisDaoSupport<EmcSampleBottleBean, Long> implements EmcSampleBottleDao {

    @Autowired
    private EmcSampleBottleMapper emcSampleBottleMapper;

    @Override
    public EmcSampleBottleMapper getMapper() {
        return emcSampleBottleMapper;
    }

}
