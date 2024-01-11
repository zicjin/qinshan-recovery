package com.sunwayworld.emc.module.bp.info.infodata.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcInfoDataDao;
import com.sunwayworld.emc.module.bp.info.infodata.mapper.EmcInfoDataMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcInfoDataDaoImpl extends MybatisDaoSupport<EmcInfoDataBean, Long>implements EmcInfoDataDao {

    @Autowired
    private EmcInfoDataMapper emcInfoDataMapper;

    @Override
    public EmcInfoDataMapper getMapper() {
        return emcInfoDataMapper;
    }

}
