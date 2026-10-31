package com.sunwayworld.emc.module.mdm.invoices.resource;

import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import org.springframework.web.bind.annotation.RequestMapping;


import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;

@RequestMapping("/secure/emc/module/mdm/invoices")
public interface EmcInvoiceResource extends GenericResource<EmcInvoiceService, EmcInvoiceBean, Long>, GenericAuditableResource<EmcInvoiceService, EmcInvoiceBean, Long> {

    Page<EmcReturnedBean> selectPaginationByInvoiceId(Long id, RestJsonWrapperBean wrapper);

    Long insert(Long id, RestJsonWrapperBean wrapper);

    void deleteReturns(RestJsonWrapperBean wrapper);
}
