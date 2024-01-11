package com.sunwayworld.emc.module.item.scm.equiptpurch.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.scm.equiptpurch.bean.EmcEquiptPurchBean;
import com.sunwayworld.emc.module.item.scm.equiptpurch.dao.EmcEquiptPurchDao;
import com.sunwayworld.emc.module.item.scm.equiptpurch.mapper.EmcEquiptPurchMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


@Repository
public class EmcEquiptPurchDaoImpl extends MybatisDaoSupport<EmcEquiptPurchBean, Long>implements EmcEquiptPurchDao {
    
    @Autowired
    private EmcEquiptPurchMapper emcEquiptPurchMapper;
    
    @Override
    public EmcEquiptPurchMapper getMapper() {
        return emcEquiptPurchMapper;
    }
    
}
