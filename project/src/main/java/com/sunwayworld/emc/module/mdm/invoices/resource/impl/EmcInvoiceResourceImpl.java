package com.sunwayworld.emc.module.mdm.invoices.resource.impl;


import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.resource.EmcInvoiceResource;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcReturnedService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.data.page.Page;
import org.springframework.web.bind.annotation.PathVariable;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("开票记录")
@RestController
@GikamBean
public class EmcInvoiceResourceImpl implements EmcInvoiceResource {

    @Autowired
    private EmcInvoiceService emcInvoiceService;

    @Autowired
    private EmcReturnedService returnedService;

    @Override
    public EmcInvoiceService getService() {
        return emcInvoiceService;
    }

    @Log(value = "新增开票记录", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "根据开票记录ID查询回款记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/returns/queries", method = RequestMethod.POST)
    public Page<EmcReturnedBean> selectPaginationByInvoiceId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return returnedService.selectPaginationByFilter(SearchFilter.instance().match("invoiceId",id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增回款记录", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/returns", method = RequestMethod.POST)
    public Long insert(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return returnedService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除回款记录", type = LogType.DELETE)
    @RequestMapping(value = "/returns", method = RequestMethod.DELETE)
    public void deleteReturns(RestJsonWrapperBean wrapper) {
        returnedService.delete(wrapper);
    }

}
