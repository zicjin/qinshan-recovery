package com.sunwayworld.emc.module.bp.info.infodata.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataItemBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcInfoDataItemDao;
import com.sunwayworld.emc.module.bp.info.infodata.mapper.EmcInfoDataItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcInfoDataItemDaoImpl extends MybatisDaoSupport<EmcInfoDataItemBean, Long>implements EmcInfoDataItemDao {
    
    @Autowired
    private EmcInfoDataItemMapper emcInfoDataItemMapper;
    
    @Override
    public EmcInfoDataItemMapper getMapper() {
        return emcInfoDataItemMapper;
    }
    
}
