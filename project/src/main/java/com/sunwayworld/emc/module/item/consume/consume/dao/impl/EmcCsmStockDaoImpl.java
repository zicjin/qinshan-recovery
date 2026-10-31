package com.sunwayworld.emc.module.item.consume.consume.dao.impl;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockDao;
import com.sunwayworld.emc.module.item.consume.consume.mapper.EmcCsmStockMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class EmcCsmStockDaoImpl extends MybatisDaoSupport<EmcCsmStockBean, Long> implements EmcCsmStockDao {

    @Autowired
    private EmcCsmStockMapper emcCsmStockMapper;
    
    @Override
    public EmcCsmStockMapper getMapper() {
        return emcCsmStockMapper;
    }
}
