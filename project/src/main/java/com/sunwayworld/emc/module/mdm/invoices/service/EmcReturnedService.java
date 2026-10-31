package com.sunwayworld.emc.module.mdm.invoices.service;


import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcReturnedService extends GenericService<EmcReturnedBean, Long> {
    Long insert(Long id, RestJsonWrapperBean wrapper);
}
