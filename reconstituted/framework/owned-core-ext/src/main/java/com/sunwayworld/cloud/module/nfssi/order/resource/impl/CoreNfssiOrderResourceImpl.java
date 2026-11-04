/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.resource.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.resource.CoreNfssiOrderResource;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiOrderService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62bd\u6837\u5355")
@RestController
@GikamBean
public class CoreNfssiOrderResourceImpl
implements CoreNfssiOrderResource,
AbstractGenericResource<CoreNfssiOrderService, CoreNfssiOrderBean, String> {
    @Autowired
    private CoreNfssiOrderService coreNfssiOrderService;

    @Override
    public CoreNfssiOrderService getService() {
        return this.coreNfssiOrderService;
    }

    @Override
    @Log(value="\u4ece\u56fd\u62bd\u5e73\u53f0\u65b0\u589e\u62bd\u6837\u5355", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u62bd\u6837\u5355\u8868ID\u67e5\u8be2\u62bd\u6837\u5355\u68c0\u6d4b\u9879\u76ee\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/order-test-items/queries"}, method={RequestMethod.POST})
    public Page<CoreNfssiOrderTestItemBean> selectOrderTestItemPaginationByOrderId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectOrderTestItemPaginationByNfssiOrderId(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u68c0\u6d4b\u9879\u76eeID\u67e5\u8be2\u68c0\u9a8c\u4f9d\u636e\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/order-test-items/{testItemId}/test-reasons/queries"}, method={RequestMethod.POST})
    public Page<CoreNfssiTestReasonBean> selectTestReasonPaginationByTestItemId(@PathVariable Long testItemId, RestJsonWrapperBean wrapper) {
        return this.coreNfssiOrderService.selectTestReasonPaginationByTestItemId(testItemId, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u68c0\u6d4b\u9879\u76eeID\u67e5\u8be2\u5224\u5b9a\u4f9d\u636e\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/order-test-items/{testItemId}/verify-reasons/queries"}, method={RequestMethod.POST})
    public Page<CoreNfssiVerifyReasonBean> selectVerifyReasonPaginationByTestItemId(@PathVariable Long testItemId, RestJsonWrapperBean wrapper) {
        return this.coreNfssiOrderService.selectVerifyReasonPaginationByTestItemId(testItemId, wrapper);
    }

    @Override
    @Log(value="\u91cd\u65b0\u8bf7\u6c42\u62bd\u6837\u5355\u6570\u636e", type=LogType.UPDATE)
    @RequestMapping(value={"/action/re-request"}, method={RequestMethod.PUT})
    public void reRequest(RestJsonWrapperBean wrapper) {
        this.coreNfssiOrderService.reRequest(wrapper);
    }
}
