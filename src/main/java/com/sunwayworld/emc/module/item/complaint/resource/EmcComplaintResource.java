package com.sunwayworld.emc.module.item.complaint.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.complaint.bean.EmcComplaintBean;
import com.sunwayworld.emc.module.item.complaint.service.EmcComplaintService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@GikamBean
@RequestMapping("/secure/emc/module/item/complaints")
public interface EmcComplaintResource extends GenericResource<EmcComplaintService, EmcComplaintBean, Long>, GenericAuditableResource<EmcComplaintService, EmcComplaintBean, Long> {

}