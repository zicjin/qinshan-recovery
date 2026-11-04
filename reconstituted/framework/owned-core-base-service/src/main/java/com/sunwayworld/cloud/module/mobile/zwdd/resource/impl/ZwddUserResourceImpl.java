/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.sunwayworld.cloud.module.mobile.zwdd.config.ZwddConfiguration;
import com.sunwayworld.cloud.module.mobile.zwdd.resource.ZwddUserResource;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.ZwddConfigStore;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.domain.ServiceResult;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.domain.UserDTO;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.taobao.api.ApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9489\u9489\u5fae\u5e94\u7528\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/zwdd/user"})
public class ZwddUserResourceImpl
implements ZwddUserResource {
    @Override
    public String getOpenId(String code, String agentid) {
        ZwddConfigStore dtService = ZwddConfiguration.getDtService(agentid);
        ServiceResult<String> accessTokenSr = dtService.getAccessToken(agentid);
        if (!accessTokenSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage()));
        }
        ServiceResult<String> userIdSr = dtService.getUserInfoByCode(agentid, code);
        if (!userIdSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(userIdSr.getCode(), userIdSr.getMessage()));
        }
        return JSONObject.toJSONString(ServiceResult.success(userIdSr.getResult()));
    }

    @Override
    public String getDtUser(String userid, String agentid) {
        OapiUserGetResponse response;
        ZwddConfigStore dtService = ZwddConfiguration.getDtService(agentid);
        ServiceResult<String> accessTokenSr = dtService.getAccessToken(agentid);
        if (!accessTokenSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage()));
        }
        String accessToken = accessTokenSr.getResult();
        DefaultDingTalkClient client = new DefaultDingTalkClient("/user/get");
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userid);
        request.setHttpMethod("GET");
        try {
            response = client.execute(request, accessToken);
        }
        catch (ApiException e) {
            return JSONObject.toJSONString(ServiceResult.failure(e.getErrCode(), "Failed to getUserName: " + e.getErrMsg()));
        }
        UserDTO user = new UserDTO();
        user.setName(response.getName());
        user.setUserid(response.getUserid());
        user.setAvatar(response.getAvatar());
        return JSONObject.toJSONString(ServiceResult.success(user));
    }

    @Override
    public String getJsSign(String agentid) {
        ZwddConfigStore dth5Service = ZwddConfiguration.getDtService(agentid);
        return JSONObject.toJSONString(dth5Service.getJsTicket(agentid));
    }
}
