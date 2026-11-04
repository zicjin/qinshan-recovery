/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.resource.LcdpApiIntegrationResource;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u63a5\u53e3\u96c6\u6210")
@RestController
@GikamBean
public class LcdpApiIntegrationResourceImpl
implements LcdpApiIntegrationResource,
AbstractGenericResource<LcdpApiIntegrationService, LcdpApiBean, Long>,
AbstractGenericActivatableResource<LcdpApiIntegrationService, LcdpApiBean, Long> {
    @Autowired
    private LcdpApiIntegrationService lcdpApiIntegrationService;

    @Override
    public LcdpApiIntegrationService getService() {
        return this.lcdpApiIntegrationService;
    }

    @Override
    @Log(value="\u65b0\u589e\u63a5\u53e3\u96c6\u6210", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u63a5\u53e3\u96c6\u6210ID\u67e5\u8be2\u63a5\u53e3\u54cd\u5e94\u5b57\u6bb5\u6620\u5c04", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Page<LcdpApiFieldBean> selectFieldPaginationByApiIntegrationId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectFieldPaginationByApiIntegrationId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u63a5\u53e3\u54cd\u5e94\u5b57\u6bb5\u6620\u5c04", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertField(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u63a5\u53e3\u54cd\u5e94\u5b57\u6bb5\u6620\u5c04", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteField(id, wrapper);
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u63a5\u53e3\u96c6\u6210\u63a5\u53e3", type=LogType.SELECT)
    @RequestMapping(value={"/{apiCode}/action/test"}, method={RequestMethod.POST})
    public Object testApi(@PathVariable String apiCode, @RequestBody JSONObject json) {
        return this.getService().testApi(apiCode, json);
    }

    @Override
    @Log(value="\u6839\u636e\u63a5\u53e3\u96c6\u6210ID\u67e5\u8be2\u63a5\u53e3\u54cd\u5e94", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/requests/queries"}, method={RequestMethod.POST})
    public Page<LcdpApiReqBean> selectRequestPaginationByApiIntegrationId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectRequestPaginationByApiIntegrationId(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u63a5\u53e3\u8bf7\u6c42ID\u67e5\u8be2\u63a5\u53e3\u54cd\u5e94", type=LogType.SELECT)
    @RequestMapping(value={"/requests/{id}/responses/queries"}, method={RequestMethod.POST})
    public Page<LcdpApiResBean> selectResponsePaginationByApiRequestId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectResponsePaginationByApiRequestId(id, wrapper);
    }

    @Override
    @Log(value="\u91cd\u8bd5\u63a5\u53e3\u8bf7\u6c42", type=LogType.UPDATE)
    @RequestMapping(value={"/requests/{id}/action/reset"}, method={RequestMethod.POST})
    public void resetRequest(@PathVariable Long id) {
        this.getService().resetRequest(id);
    }

    @Override
    @Log(value="\u6821\u9a8curl", type=LogType.VALIDATE)
    @RequestMapping(value={"/{id}/action/validate-url"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUrl(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().validateUrl(id, wrapper);
    }
}
