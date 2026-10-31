package com.sunwayworld.emc.module.mdm.invoices.service;

import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;

public interface EmcInvoiceService extends GenericService<EmcInvoiceBean, Long>, GenericAuditableService<EmcInvoiceBean, Long> {

}
