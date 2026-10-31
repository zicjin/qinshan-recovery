package com.sunwayworld.emc.module.bp.order.orderretain.service.impl;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.emc.module.bp.order.orderretain.dao.EmcOrderRetainDao;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.DateTimeUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class EmcOrderRetainServiceImpl
implements EmcOrderRetainService {
    @Autowired
    private EmcOrderRetainDao orderRetainDao;

    public EmcOrderRetainDao getDao() {
        return this.orderRetainDao;
    }

    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcOrderRetainBean emcOrderRetainBean = (EmcOrderRetainBean)wrapperBean.parseUnique(EmcOrderRetainBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcOrderRetainBean.setId(id);
        this.getDao().insert(emcOrderRetainBean);
        return id;
    }

    public void retainOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderRetainBean> emcOrderRetainBeanList = wrapper.parse(EmcOrderRetainBean.class);
        emcOrderRetainBeanList.forEach(emcOrderRetainBean -> {
            emcOrderRetainBean.setOrderId(emcOrderRetainBean.getId());
            emcOrderRetainBean.setId(ApplicationContextHelper.getNextIdentity());
            emcOrderRetainBean.setExt$Item("isretain", "true");
            emcOrderRetainBean.setRetainTimeLimit(DateTimeUtils.plusDays((LocalDateTime)LocalDateTime.now(), (Long)emcOrderRetainBean.getRetainDays()));
        });
        this.getDao().insert(emcOrderRetainBeanList);
    }

    public Page<EmcOrderRetainOverdueBean> selectOverdueOrderRetain(RestJsonWrapperBean wrapper) {
        List beans = this.getDao().selectOverdueOrderRetain();
        return new Pagination(beans);
    }
}
