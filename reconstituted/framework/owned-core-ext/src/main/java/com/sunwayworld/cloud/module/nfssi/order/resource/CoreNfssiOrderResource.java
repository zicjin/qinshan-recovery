/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.resource;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderTestItemBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/nfssi/orders"})
public interface CoreNfssiOrderResource
extends GenericCloudResource<CoreNfssiOrderBean, String> {
    public Page<CoreNfssiOrderTestItemBean> selectOrderTestItemPaginationByOrderId(String var1, RestJsonWrapperBean var2);

    public Page<CoreNfssiTestReasonBean> selectTestReasonPaginationByTestItemId(Long var1, RestJsonWrapperBean var2);

    public Page<CoreNfssiVerifyReasonBean> selectVerifyReasonPaginationByTestItemId(Long var1, RestJsonWrapperBean var2);

    public void reRequest(RestJsonWrapperBean var1);
}
