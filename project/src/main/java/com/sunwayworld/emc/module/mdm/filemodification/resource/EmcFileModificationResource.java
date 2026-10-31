package com.sunwayworld.emc.module.mdm.filemodification.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.filemodification.bean.EmcFileModificationBean;
import com.sunwayworld.emc.module.mdm.filemodification.service.EmcFileModificationService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
@RequestMapping("/secure/emc/module/mdm/file-modifications")
public interface EmcFileModificationResource extends GenericResource<EmcFileModificationService, EmcFileModificationBean, Long>, GenericAuditableResource<EmcFileModificationService, EmcFileModificationBean, Long> ,GenericChoosableResource<EmcFileModificationService, EmcFileModificationBean, Long>{

}
