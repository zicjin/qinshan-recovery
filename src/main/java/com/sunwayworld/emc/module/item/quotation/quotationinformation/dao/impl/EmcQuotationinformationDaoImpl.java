package com.sunwayworld.emc.module.item.quotation.quotationinformation.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.quotation.quotationinformation.bean.EmcQuotationinformationBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.dao.EmcQuotationinformationDao;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.mapper.EmcQuotationinformationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcQuotationinformationDaoImpl extends MybatisDaoSupport<EmcQuotationinformationBean, Long> implements EmcQuotationinformationDao {

    @Autowired
    private EmcQuotationinformationMapper emcQuotationinformationMapper;

    @Override
    public EmcQuotationinformationMapper getMapper() {
        return emcQuotationinformationMapper;
    }

}
