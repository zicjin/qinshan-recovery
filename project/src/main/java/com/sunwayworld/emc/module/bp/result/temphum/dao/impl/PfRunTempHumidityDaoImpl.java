package com.sunwayworld.emc.module.bp.result.temphum.dao.impl;



import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.dao.PfRunTempHumidityDao;
import com.sunwayworld.emc.module.bp.result.temphum.mapper.PfRunTempHumidityMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2022年01月10日
 */
@Repository
@GikamBean
public class PfRunTempHumidityDaoImpl extends MybatisDaoSupport<PfRunTempHumidityBean, Long> implements PfRunTempHumidityDao {

    @Autowired
    private PfRunTempHumidityMapper pfRunTempHumidityMapper;

    @Override
    public PfRunTempHumidityMapper getMapper() {
        return pfRunTempHumidityMapper;
    }

}
