package com.sunwayworld.emc.module.bp.info.infodata.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcWaterResultBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcWaterResultDao;
import com.sunwayworld.emc.module.bp.info.infodata.mapper.EmcWaterResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcWaterResultDaoImpl extends MybatisDaoSupport<EmcWaterResultBean, Long> implements EmcWaterResultDao {

    @Autowired
    private EmcWaterResultMapper emcWaterResultMapper;

    @Override
    public EmcWaterResultMapper getMapper() {
        return emcWaterResultMapper;
    }

}
