/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.resource.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.resource.LcdpApiResource;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@GikamBean
public class LcdpApiResourceImpl
implements LcdpApiResource {
    @Autowired
    private LcdpApiIntegrationService lcdpApiIntegrationService;

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u63a5\u53e3\u96c6\u6210restful\u5f00\u653e\u63a5\u53e3")
    @RequestMapping(path={"/api/**"}, method={RequestMethod.GET, RequestMethod.POST}, produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public Object openApi(@RequestBody(required=false) String requestBody, HttpServletRequest request) {
        return this.lcdpApiIntegrationService.callRestfulApi(ServletUtils.getRequestUri(request), requestBody);
    }

    @Override
    @Log(value="\u4f4e\u4ee3\u7801\u63a5\u53e3\u96c6\u6210webservice\u5f00\u653e\u63a5\u53e3")
    @RequestMapping(path={"/ws/**"}, method={RequestMethod.GET, RequestMethod.POST})
    public void openSoap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.lcdpApiIntegrationService.callSoapApi(request, response);
    }
}
