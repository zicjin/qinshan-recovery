package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.EmcDataCorrelationDao;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.mapper.EmcDataCorrelationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@Repository
@GikamBean
public class EmcDataCorrelationDaoImpl extends MybatisDaoSupport<EmcDataCorrelationBean, Long>implements EmcDataCorrelationDao {
    
    @Autowired
    private EmcDataCorrelationMapper dataCorrelationMapper;
    
    @Override
    public EmcDataCorrelationMapper getMapper() {
        return dataCorrelationMapper;
    }
    
}
