package com.sunwayworld.emc.module.item.complaint.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.complaint.bean.EmcComplaintBean;
import com.sunwayworld.emc.module.item.complaint.dao.EmcComplaintDao;
import com.sunwayworld.emc.module.item.complaint.service.EmcComplaintService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@Repository
@GikamBean
public class EmcComplaintServiceImpl implements EmcComplaintService {
    @Autowired
    private EmcComplaintDao complaintDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcComplaintDao getDao() {
        return complaintDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcComplaintBean complaint = jsonWrapper.parseUnique(EmcComplaintBean.class);
        complaint.setId(ApplicationContextHelper.getNextIdentity());
        complaint.setQualityControlNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(complaint);
        return complaint.getId();
    }
}
