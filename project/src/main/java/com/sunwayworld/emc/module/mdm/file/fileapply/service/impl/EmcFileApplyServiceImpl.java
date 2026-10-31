package com.sunwayworld.emc.module.mdm.file.fileapply.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.file.fileapply.bean.EmcFileApplyBean;
import com.sunwayworld.emc.module.mdm.file.fileapply.dao.EmcFileApplyDao;
import com.sunwayworld.emc.module.mdm.file.fileapply.service.EmcFileApplyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcFileApplyServiceImpl implements EmcFileApplyService {
    @Autowired
    private EmcFileApplyDao fileApplyDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFileApplyDao getDao() {
        return fileApplyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcFileApplyBean emcFileApplyBean = wrapper.parseUnique(EmcFileApplyBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFileApplyBean.setId(id);
        getDao().insert(emcFileApplyBean);
        return id;
    }

}
