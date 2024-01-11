package com.sunwayworld.emc.module.item.consume.consume.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockLogDao;
import com.sunwayworld.emc.module.item.consume.consume.mapper.EmcCsmStockLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcCsmStockLogDaoImpl extends MybatisDaoSupport<EmcCsmStockLogBean, Long> implements EmcCsmStockLogDao {

    @Autowired
    private EmcCsmStockLogMapper emcCsmStockLogMapper;
    
    @Override
    public EmcCsmStockLogMapper getMapper() {
        return emcCsmStockLogMapper;
    }
}
