/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.service.impl;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.cloud.module.qms.applycomplaint.persistent.dao.CoreApplyComplaintDao;
import com.sunwayworld.cloud.module.qms.applycomplaint.service.CoreApplyComplaintService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreApplyComplaintServiceImpl
implements CoreApplyComplaintService {
    @Autowired
    private CoreApplyComplaintDao coreApplyComplaintDao;

    @Override
    public CoreApplyComplaintDao getDao() {
        return this.coreApplyComplaintDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreApplyComplaintBean coreApplyComplaint = jsonWrapper.parseUnique(CoreApplyComplaintBean.class);
        coreApplyComplaint.setId(ApplicationContextHelper.getNextIdentity());
        coreApplyComplaint.setApplyComplaintNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "STS"));
        this.getDao().insert(coreApplyComplaint);
        return coreApplyComplaint.getId();
    }

    @Override
    public List<Map<String, Object>> selectAuditStatusByCondition(MapperParameter parameter) {
        return this.getDao().selectAuditStatusByCondition(parameter);
    }
}
