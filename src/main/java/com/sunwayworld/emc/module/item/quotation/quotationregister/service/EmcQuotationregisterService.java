package com.sunwayworld.emc.module.item.quotation.quotationregister.service;

import com.sunwayworld.emc.module.item.quotation.quotationregister.bean.EmcQuotationregisterBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcQuotationregisterService extends GenericService<EmcQuotationregisterBean, Long>, GenericAuditableService<EmcQuotationregisterBean, Long> {
    void addDiscount(RestJsonWrapperBean wrapper);

    void calculation(RestJsonWrapperBean wrapper);

    void addContract(RestJsonWrapperBean wrapper);
}
