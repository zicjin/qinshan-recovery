package com.sunwayworld.emc.module.item.complaint.service;

import com.sunwayworld.emc.module.item.complaint.bean.EmcComplaintBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

public interface EmcComplaintService extends GenericService<EmcComplaintBean, Long>, GenericAuditableService<EmcComplaintBean, Long> {

}
