/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.service.impl;

import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.cloud.module.qms.filevolume.persistent.dao.CoreFileVolumeDao;
import com.sunwayworld.cloud.module.qms.filevolume.service.CoreFileVolumeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreFileVolumeServiceImpl
implements CoreFileVolumeService {
    @Autowired
    private CoreFileVolumeDao coreFileVolumeDao;

    @Override
    public CoreFileVolumeDao getDao() {
        return this.coreFileVolumeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileVolumeBean coreFileVolume = jsonWrapper.parseUnique(CoreFileVolumeBean.class);
        coreFileVolume.setId(ApplicationContextHelper.getNextIdentity());
        coreFileVolume.setActivatedFlag("2");
        this.getDao().insert(coreFileVolume);
        return coreFileVolume.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.ACTIVATE)
    @Audit(value="AUDIT.ACTIVATE")
    public void activate(RestJsonWrapperBean wrapper) {
        List<CoreFileVolumeBean> coreFileVolumeList = wrapper.parse(CoreFileVolumeBean.class);
        coreFileVolumeList.forEach(i -> {
            this.setActivateProperties(i, true);
            i.setDeactivatedTime(null);
        });
        this.getDao().update(coreFileVolumeList, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME", "DEACTIVATEDTIME"});
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DEACTIVATE)
    @Audit(value="AUDIT.DEACTIVATE")
    public void deactivate(RestJsonWrapperBean wrapper) {
        List<CoreFileVolumeBean> coreFileVolumeList = wrapper.parse(CoreFileVolumeBean.class);
        coreFileVolumeList.forEach(i -> {
            this.setActivateProperties(i, false);
            i.setDeactivatedTime(LocalDateTime.now());
        });
        this.getDao().update(coreFileVolumeList, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME", "DEACTIVATEDTIME"});
    }
}
