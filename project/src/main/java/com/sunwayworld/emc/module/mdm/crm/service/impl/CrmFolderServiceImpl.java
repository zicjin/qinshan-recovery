package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.crm.dao.CrmFolderDao;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmFolderBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmFolderService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class CrmFolderServiceImpl implements CrmFolderService {

    @Autowired
    private CrmFolderDao crmFolderDao;

    @Override
    public CrmFolderDao getDao() {
        return crmFolderDao;
    }

}
