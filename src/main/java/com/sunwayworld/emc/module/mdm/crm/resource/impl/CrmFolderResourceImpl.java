package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmFolderResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmFolderService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("crm核销表")
@RestController
@GikamBean
public class CrmFolderResourceImpl implements CrmFolderResource {

    @Autowired
    private CrmFolderService crmFolderService;

    @Override
    public CrmFolderService getService() {
        return crmFolderService;
    }


}
