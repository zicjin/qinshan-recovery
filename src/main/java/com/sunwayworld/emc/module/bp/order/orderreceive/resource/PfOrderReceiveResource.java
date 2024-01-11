package com.sunwayworld.emc.module.bp.order.orderreceive.resource;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.emc.module.bp.mobile.resource.EmcMobileProjectResource;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/secure/emc/module/bp/order/order-receive")
public interface PfOrderReceiveResource{

    @Log(value = "扫码匹配", type = LogType.UPDATE)
    @RequestMapping(value = "/action/match", method = RequestMethod.POST)
    Long match(RestJsonWrapperBean wrapper);

    @Log(value = "样品领用提交", type = LogType.UPDATE)
    @RequestMapping(value = "/action/receive", method = RequestMethod.PUT)
    Long receive(RestJsonWrapperBean wrapper);
}
