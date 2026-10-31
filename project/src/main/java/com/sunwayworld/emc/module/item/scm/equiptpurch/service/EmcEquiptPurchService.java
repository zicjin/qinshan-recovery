package com.sunwayworld.emc.module.item.scm.equiptpurch.service;

import com.sunwayworld.emc.module.item.scm.equiptpurch.bean.EmcEquiptPurchBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptPurchService extends GenericService<EmcEquiptPurchBean, Long>,GenericAuditableService<EmcEquiptPurchBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
