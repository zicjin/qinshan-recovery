package com.sunwayworld.emc.module.item.quotation.discount.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.quotation.discount.dao.EmcDiscountDao;
import com.sunwayworld.emc.module.item.quotation.discount.bean.EmcDiscountBean;
import com.sunwayworld.emc.module.item.quotation.discount.service.EmcDiscountService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcDiscountServiceImpl implements EmcDiscountService {

    @Autowired
    private EmcDiscountDao emcDiscountDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcDiscountDao getDao() {
        return emcDiscountDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcDiscountBean emcDiscount = jsonWrapper.parseUnique(EmcDiscountBean.class);
        emcDiscount.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcDiscount);
        return emcDiscount.getId();
    }

}
