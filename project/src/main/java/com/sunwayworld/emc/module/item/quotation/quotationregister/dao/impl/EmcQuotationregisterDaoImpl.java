package com.sunwayworld.emc.module.item.quotation.quotationregister.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.quotation.quotationregister.bean.EmcQuotationregisterBean;
import com.sunwayworld.emc.module.item.quotation.quotationregister.dao.EmcQuotationregisterDao;
import com.sunwayworld.emc.module.item.quotation.quotationregister.mapper.EmcQuotationregisterMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcQuotationregisterDaoImpl extends MybatisDaoSupport<EmcQuotationregisterBean, Long> implements EmcQuotationregisterDao {

    @Autowired
    private EmcQuotationregisterMapper emcQuotationregisterMapper;

    @Override
    public EmcQuotationregisterMapper getMapper() {
        return emcQuotationregisterMapper;
    }

}
