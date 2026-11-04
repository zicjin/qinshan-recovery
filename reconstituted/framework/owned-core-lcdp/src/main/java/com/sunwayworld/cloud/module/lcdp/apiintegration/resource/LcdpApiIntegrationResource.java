/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.resource;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/api-integrations"})
public interface LcdpApiIntegrationResource
extends GenericCloudResource<LcdpApiBean, Long>,
GenericActivatableCloudResource<LcdpApiBean, Long> {
    public Page<LcdpApiFieldBean> selectFieldPaginationByApiIntegrationId(Long var1, RestJsonWrapperBean var2);

    public Long insertField(Long var1, RestJsonWrapperBean var2);

    public void deleteField(Long var1, RestJsonWrapperBean var2);

    public Object testApi(String var1, JSONObject var2);

    public Page<LcdpApiReqBean> selectRequestPaginationByApiIntegrationId(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpApiResBean> selectResponsePaginationByApiRequestId(Long var1, RestJsonWrapperBean var2);

    public void resetRequest(Long var1);

    public RestValidationResultBean validateUrl(Long var1, RestJsonWrapperBean var2);
}
