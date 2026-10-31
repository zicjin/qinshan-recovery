package com.sunwayworld.emc.module.bp.order.orderlabel.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.dao.EmcOrderLabelDao;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@GikamBean
@Repository
public class EmcOrderLabelServiceImpl implements EmcOrderLabelService {

    @Autowired
    private EmcOrderLabelDao orderLabelDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcOrderLabelDao getDao() {
        return orderLabelDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcOrderLabelBean emcOrderLabelBean = wrapperBean.parseUnique(EmcOrderLabelBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        RestValidationResultBean validateUnique = validateUnique(id, "LABELCODE", emcOrderLabelBean.getLabelCode());
        if (!validateUnique.getValid()) {
            return 0L;
        }
        emcOrderLabelBean.setId(id);
        emcOrderLabelBean.setMatch("0");
        getDao().insert(emcOrderLabelBean);
        return id;
    }

}
