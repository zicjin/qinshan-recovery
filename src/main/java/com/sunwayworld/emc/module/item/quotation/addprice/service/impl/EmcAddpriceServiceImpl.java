package com.sunwayworld.emc.module.item.quotation.addprice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.quotation.addprice.dao.EmcAddpriceDao;
import com.sunwayworld.emc.module.item.quotation.addprice.bean.EmcAddpriceBean;
import com.sunwayworld.emc.module.item.quotation.addprice.service.EmcAddpriceService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcAddpriceServiceImpl implements EmcAddpriceService {

    @Autowired
    private EmcAddpriceDao emcAddpriceDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcAddpriceDao getDao() {
        return emcAddpriceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcAddpriceBean emcAddprice = jsonWrapper.parseUnique(EmcAddpriceBean.class);
        emcAddprice.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcAddprice);
        return emcAddprice.getId();
    }

}
