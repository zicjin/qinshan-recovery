package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.EmcDataCorrelationItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.mapper.EmcDataCorrelationItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@Repository
@GikamBean
public class EmcDataCorrelationItemDaoImpl extends MybatisDaoSupport<EmcDataCorrelationItemBean, Long> implements EmcDataCorrelationItemDao {

    @Autowired
    private EmcDataCorrelationItemMapper dataCorrelationItemMapper;

    @Override
    public EmcDataCorrelationItemMapper getMapper() {
        return dataCorrelationItemMapper;
    }

}
