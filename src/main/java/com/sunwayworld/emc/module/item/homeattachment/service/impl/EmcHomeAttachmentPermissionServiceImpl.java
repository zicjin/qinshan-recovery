package com.sunwayworld.emc.module.item.homeattachment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentPermissionDao;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentPermissionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcHomeAttachmentPermissionServiceImpl implements EmcHomeAttachmentPermissionService {

    @Autowired
    private EmcHomeAttachmentPermissionDao emcHomeAttachmentPermissionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcHomeAttachmentPermissionDao getDao() {
        return emcHomeAttachmentPermissionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcHomeAttachmentPermissionBean> permissionList = jsonWrapper.parse(EmcHomeAttachmentPermissionBean.class);
        permissionList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(permissionList);
        return permissionList.get(0).getId();
    }

}
