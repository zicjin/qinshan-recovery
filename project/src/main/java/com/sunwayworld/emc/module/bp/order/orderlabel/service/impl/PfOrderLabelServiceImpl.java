package com.sunwayworld.emc.module.bp.order.orderlabel.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.dao.EmcOrderLabelDao;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021/10/15
 */
@Repository
@GikamBean
public class PfOrderLabelServiceImpl extends EmcOrderLabelServiceImpl implements EmcOrderLabelService  {

    @Autowired
    private EmcOrderLabelDao orderLabelDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcOrderLabelDao getDao() {
        return orderLabelDao;
    }

    @Autowired
    private EmcOrderContainerService orderContainerService;

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
        //扫描的样品更新样品表的状态为已扫描
        String labelCode = emcOrderLabelBean.getLabelCode().toUpperCase();

        List<EmcOrderContainerBean> orderContainerList = orderContainerService.getDao().selectListByOneColumnValue(labelCode, "orderContainerNo");
        if (!orderContainerList.isEmpty()) {
            orderContainerList.forEach(oc -> {
                oc.setIsScan(Constant.YES);
                oc.setAcceptedDate(LocalDateTime.now());
                oc.setAcceptedUserId(LocalContextHelper.getLoginUserId());
                oc.setAcceptedUserName(LocalContextHelper.getLoginUserName());
            });
            orderContainerService.getDao().update(orderContainerList, "isScan", "acceptedDate", "acceptedUserId", "acceptedUserName");
        }

        emcOrderLabelBean.setId(id);
        emcOrderLabelBean.setLabelCode(labelCode);
        emcOrderLabelBean.setMatch("0");
        getDao().insert(emcOrderLabelBean);

        return id;
    }

}
