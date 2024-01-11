package com.sunwayworld.emc.module.bp.result.run.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcQcRelatedOrderDao;
import com.sunwayworld.emc.module.bp.result.run.service.EmcQcRelatedOrderService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年12月08日
*/
@Repository
public class EmcQcRelatedOrderServiceImpl implements EmcQcRelatedOrderService {

    @Autowired
    private EmcQcRelatedOrderDao emcQcRelatedOrderDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQcRelatedOrderDao getDao() {
        return emcQcRelatedOrderDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcQcRelatedOrderBean emcQcRelatedOrder = wrapper.parseUnique(EmcQcRelatedOrderBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcQcRelatedOrder.setId(id);
        getDao().insert(emcQcRelatedOrder);

        return id;
    }

}
