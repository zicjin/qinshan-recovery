package com.sunwayworld.emc.module.mdm.crm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmFolderBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmFolderService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/crm/crmfolder")
public interface CrmFolderResource extends GenericResource<CrmFolderService, CrmFolderBean, String> {
}
