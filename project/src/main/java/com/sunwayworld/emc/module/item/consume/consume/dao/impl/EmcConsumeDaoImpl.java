package com.sunwayworld.emc.module.item.consume.consume.dao.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcConsumeDao;
import com.sunwayworld.emc.module.item.consume.consume.mapper.EmcConsumeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
@GikamBean
public class EmcConsumeDaoImpl extends MybatisDaoSupport<EmcConsumeBean, String> implements EmcConsumeDao {

    @Autowired
    private EmcConsumeMapper emcConsumeMapper;

    @Override
    public EmcConsumeMapper getMapper() {
        return emcConsumeMapper;
    }

    @Override
    public void updateStockQty(String id) {
        getMapper().updateStockQty(id);
    }
}
