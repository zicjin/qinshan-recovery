package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcResultLogDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcResultLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-21
 */

@Repository
@GikamBean
public class EmcResultLogServiceImpl implements EmcResultLogService {
    @Autowired
    private EmcResultLogDao resultLogDao;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcResultLogDao getDao() {
        return resultLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long id, RestJsonWrapperBean jsonWrapper) {
        EmcResultLogBean resultLog = jsonWrapper.parseUnique(EmcResultLogBean.class);

        // 新增结果记录
        resultLog.setId(ApplicationContextHelper.getNextIdentity());
        EmcOrderTaskResultBean orderTaskResult = orderTaskResultService.selectById(resultLog.getResultId());
        resultLog.setPreviousValue(orderTaskResult.getOriginalResult());
        getDao().insert(resultLog);
        return resultLog.getId();
    }

}
