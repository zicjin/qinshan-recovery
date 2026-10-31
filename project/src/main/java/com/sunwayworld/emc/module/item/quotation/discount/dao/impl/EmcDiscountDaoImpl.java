package com.sunwayworld.emc.module.item.quotation.discount.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.quotation.discount.bean.EmcDiscountBean;
import com.sunwayworld.emc.module.item.quotation.discount.dao.EmcDiscountDao;
import com.sunwayworld.emc.module.item.quotation.discount.mapper.EmcDiscountMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcDiscountDaoImpl extends MybatisDaoSupport<EmcDiscountBean, Long> implements EmcDiscountDao {

    @Autowired
    private EmcDiscountMapper emcDiscountMapper;

    @Override
    public EmcDiscountMapper getMapper() {
        return emcDiscountMapper;
    }

}
