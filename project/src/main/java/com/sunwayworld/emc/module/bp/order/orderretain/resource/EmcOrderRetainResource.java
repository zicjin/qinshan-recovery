package com.sunwayworld.emc.module.bp.order.orderretain.resource;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.data.page.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@GikamBean
@RequestMapping("/secure/emc/module/bp/order/order-retains")
public interface EmcOrderRetainResource extends GenericResource<EmcOrderRetainService, EmcOrderRetainBean, Long>, GenericAuditableResource<EmcOrderRetainService, EmcOrderRetainBean, Long> {
    @RequestMapping(value = "/action/handle", method = RequestMethod.POST)
    void handleRetainOrder(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/action/retain", method = RequestMethod.POST)
    void retainOrder(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/queries-overdue", method = RequestMethod.POST)
    Page<EmcOrderRetainOverdueBean> selectOverdueOrderRetain(RestJsonWrapperBean wrapper);
}
