package com.sunwayworld.emc.module.mdm.org.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/org-permissions")
public interface EmcOrgPermissionResource extends GenericResource<EmcOrgPermissionService, EmcOrgPermissionBean, Long> {

}
