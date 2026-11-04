/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.sunwayworld.cloud.module.mobile.dingtalk.config.DingtalkConfiguration;
import com.sunwayworld.cloud.module.mobile.dingtalk.resource.DingtalkUserResource;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.DingtalkConfigStore;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.domain.ServiceResult;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.taobao.api.ApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9489\u9489\u5fae\u5e94\u7528\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/dingtalk/user"})
public class DingtalkUserResourceImpl
implements DingtalkUserResource {
    @Override
    public String getOpenId(String code, String agentid) {
        DingtalkConfigStore dtService = DingtalkConfiguration.getDtService(agentid);
        ServiceResult<String> accessTokenSr = dtService.getAccessToken();
        if (!accessTokenSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage()));
        }
        String accessToken = accessTokenSr.getResult();
        ServiceResult<String> userIdSr = this.getUserInfo(accessToken, code);
        if (!userIdSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(userIdSr.getCode(), userIdSr.getMessage()));
        }
        return JSONObject.toJSONString(ServiceResult.success(userIdSr.getResult()));
    }

    @Override
    public String getDtUser(String userid, String agentid) {
        OapiUserGetResponse response;
        DingtalkConfigStore dtService = DingtalkConfiguration.getDtService(agentid);
        ServiceResult<String> accessTokenSr = dtService.getAccessToken();
        if (!accessTokenSr.isSuccess()) {
            return JSONObject.toJSONString(ServiceResult.failure(accessTokenSr.getCode(), accessTokenSr.getMessage()));
        }
        String accessToken = accessTokenSr.getResult();
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userid);
        request.setHttpMethod("GET");
        try {
            response = client.execute(request, accessToken);
        }
        catch (ApiException e) {
            return JSONObject.toJSONString(ServiceResult.failure(e.getErrCode(), "Failed to getUserName: " + e.getErrMsg()));
        }
        return JSONObject.toJSONString(ServiceResult.success(response));
    }

    @Override
    public String getJsSign(String agentid) {
        DingtalkConfigStore dth5Service = DingtalkConfiguration.getDtService(agentid);
        return JSONObject.toJSONString(dth5Service.getJsTicket());
    }

    private ServiceResult<String> getUserInfo(String accessToken, String authCode) {
        OapiUserGetuserinfoResponse response;
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(authCode);
        request.setHttpMethod("GET");
        try {
            response = client.execute(request, accessToken);
        }
        catch (ApiException e) {
            e.printStackTrace();
            return ServiceResult.failure(e.getErrCode(), "Failed to getUserInfo: " + e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }
        return ServiceResult.success(response.getUserid());
    }
}
