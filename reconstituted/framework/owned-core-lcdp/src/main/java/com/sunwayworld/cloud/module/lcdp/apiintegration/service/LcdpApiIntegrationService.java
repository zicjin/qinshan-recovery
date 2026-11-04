/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LcdpApiIntegrationService
extends GenericService<LcdpApiBean, Long>,
GenericActivatableService<LcdpApiBean, Long> {
    public Page<LcdpApiFieldBean> selectFieldPaginationByApiIntegrationId(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpApiReqBean> selectRequestPaginationByApiIntegrationId(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpApiResBean> selectResponsePaginationByApiRequestId(Long var1, RestJsonWrapperBean var2);

    public Long insertField(Long var1, RestJsonWrapperBean var2);

    public void deleteField(Long var1, RestJsonWrapperBean var2);

    public LcdpApiBean selectApiByCode(String var1);

    public LcdpApiBean selectActivatedApiByUrl(String var1);

    public List<LcdpApiFieldBean> selectCacheableApiFieldList(Long var1);

    public Object testApi(String var1, JSONObject var2);

    public Object callRestfulApi(String var1, String var2);

    public void resetRequest(Long var1);

    public void callSoapApi(HttpServletRequest var1, HttpServletResponse var2) throws Exception;

    public Object callApiScript(String var1, Object ... var2);

    public RestValidationResultBean validateUrl(Long var1, RestJsonWrapperBean var2);
}
