package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskLogDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-22
 */

@Repository
@GikamBean
public class EmcOrderTaskLogServiceImpl implements EmcOrderTaskLogService {
    @Autowired
    private EmcOrderTaskLogDao orderTaskLogDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderTaskLogDao getDao() {
        return orderTaskLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(List<EmcOrderTaskBean> allOrderTaskList, String operateCategory, String model) {
        List<EmcOrderTaskLogBean> orderTaskLogList = new ArrayList<>();
        allOrderTaskList.forEach(e -> {
            EmcOrderTaskLogBean orderTaskLog = new EmcOrderTaskLogBean();
            orderTaskLog.setId(ApplicationContextHelper.getNextIdentity());
            orderTaskLog.setFolderId(e.getFolderId());
            orderTaskLog.setOrderId(e.getOrderId());
            orderTaskLog.setOrderTaskId(e.getId());
            orderTaskLog.setTestId(e.getTestId());
            orderTaskLog.setMethodName(e.getMethodName());
            orderTaskLog.setOperateCategory(operateCategory);
            orderTaskLog.setModule(model);
            orderTaskLogList.add(orderTaskLog);
        });
        getDao().insert(orderTaskLogList);

        if (!orderTaskLogList.isEmpty()) {
            return orderTaskLogList.get(0).getId();
        }

        return null;
    }

}
