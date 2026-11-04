/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreNfssiOrderService
extends GenericService<CoreNfssiOrderBean, String> {
    public Page<CoreNfssiOrderTestItemBean> selectOrderTestItemPaginationByNfssiOrderId(String var1, RestJsonWrapperBean var2);

    public Page<CoreNfssiTestReasonBean> selectTestReasonPaginationByTestItemId(Long var1, RestJsonWrapperBean var2);

    public Page<CoreNfssiVerifyReasonBean> selectVerifyReasonPaginationByTestItemId(Long var1, RestJsonWrapperBean var2);

    public void reRequest(RestJsonWrapperBean var1);
}
