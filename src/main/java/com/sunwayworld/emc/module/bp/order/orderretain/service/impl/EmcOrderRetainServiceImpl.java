package com.sunwayworld.emc.module.bp.order.orderretain.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.dao.EmcOrderRetainDao;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@GikamBean
@Repository
public class EmcOrderRetainServiceImpl implements EmcOrderRetainService {

    @Autowired
    private EmcOrderRetainDao orderRetainDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcOrderRetainDao getDao() {
        return orderRetainDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcOrderRetainBean emcOrderRetainBean = wrapperBean.parseUnique(EmcOrderRetainBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcOrderRetainBean.setId(id);
        getDao().insert(emcOrderRetainBean);

        return id;
    }

    @Override
    public void retainOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderRetainBean> emcOrderRetainBeanList = wrapper.parse(EmcOrderRetainBean.class);
        emcOrderRetainBeanList.forEach(emcOrderRetainBean -> {
            emcOrderRetainBean.setOrderId(emcOrderRetainBean.getId());
            emcOrderRetainBean.setId(ApplicationContextHelper.getNextIdentity());
            emcOrderRetainBean.setRetainTimeLimit(DateTimeUtils.plusDays(LocalDateTime.now(), emcOrderRetainBean.getRetainDays()));
        });
        getDao().insert(emcOrderRetainBeanList);
    }

    @Override
    public Page<EmcOrderRetainOverdueBean> selectOverdueOrderRetain(RestJsonWrapperBean wrapper) {
        List<EmcOrderRetainOverdueBean> beans = getDao().selectOverdueOrderRetain();
        return new Pagination<>(beans);
    }

}
