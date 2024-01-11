package com.sunwayworld.emc.module.mdm.file.fileapply.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.file.fileapply.bean.EmcFileApplyBean;
import com.sunwayworld.emc.module.mdm.file.fileapply.service.EmcFileApplyService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/file/file-applies")
public interface EmcFileApplyResource extends GenericResource<EmcFileApplyService, EmcFileApplyBean, Long>, GenericAuditableResource<EmcFileApplyService, EmcFileApplyBean, Long> {

}