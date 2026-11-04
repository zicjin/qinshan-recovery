/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.sunwayworld.cloud.module.mobile.dingtalk.config.DingtalkProperties;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.domain.ServiceResult;
import com.taobao.api.ApiException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DingtalkConfigStore {
    private static final Logger log = LoggerFactory.getLogger(DingtalkConfigStore.class);
    private static HashMap<String, JSONObject> tokenMap = new HashMap();
    private static HashMap<String, JSONObject> jsapiMap = new HashMap();
    private static final long CACHE_TTL = 6600000L;
    private DingtalkProperties.DingtalkAppConfig appConfig;

    public DingtalkConfigStore(DingtalkProperties.DingtalkAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public ServiceResult<String> getAccessToken() {
        OapiGettokenResponse response;
        String accessToken = this.getFromCache("accessToken", "access_token");
        if (accessToken != null) {
            return ServiceResult.success(accessToken);
        }
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(this.appConfig.getAppKey());
        request.setAppsecret(this.appConfig.getAppSecret());
        request.setHttpMethod("GET");
        try {
            response = client.execute(request);
        }
        catch (ApiException e) {
            log.error("getAccessToken failed", e);
            return ServiceResult.failure(e.getErrCode(), e.getErrMsg());
        }
        accessToken = response.getAccessToken();
        this.putToCache("accessToken", "access_token", accessToken);
        return ServiceResult.success(accessToken);
    }

    public ServiceResult<String> getJsTicket() {
        OapiGetJsapiTicketResponse response;
        String ticket = this.getFromCache("jsticket", "ticket");
        if (ticket != null) {
            return ServiceResult.success(ticket);
        }
        ServiceResult<String> tokenSr = this.getAccessToken();
        if (!tokenSr.isSuccess()) {
            return ServiceResult.failure(tokenSr.getCode(), tokenSr.getMessage());
        }
        String accessToken = tokenSr.getResult();
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket");
        OapiGetJsapiTicketRequest request = new OapiGetJsapiTicketRequest();
        request.setHttpMethod("GET");
        try {
            response = client.execute(request, accessToken);
        }
        catch (ApiException e) {
            log.error("getAccessToken failed", e);
            return ServiceResult.failure(e.getErrCode(), e.getErrMsg());
        }
        if (!response.isSuccess()) {
            return ServiceResult.failure(response.getErrorCode(), response.getErrmsg());
        }
        ticket = response.getTicket();
        this.putToCache("jsticket", "ticket", ticket);
        return ServiceResult.success(ticket);
    }

    private String getFromCache(String section, String agentid) {
        if (section.equals("accessToken")) {
            JSONObject o = tokenMap.get(agentid);
            if (o != null && System.currentTimeMillis() - o.getLong("begin_time") <= 6600000L) {
                return o.getString("access_token");
            }
            return null;
        }
        JSONObject o = jsapiMap.get(agentid);
        if (o != null && System.currentTimeMillis() - o.getLong("begin_time") <= 6600000L) {
            return o.getString("ticket");
        }
        return null;
    }

    private void putToCache(String section, String agentid, String value) {
        JSONObject fieldObj = new JSONObject(2);
        fieldObj.put("begin_time", (Object)System.currentTimeMillis());
        if (section.equals("accessToken")) {
            fieldObj.put("access_token", (Object)value);
            tokenMap.put(agentid, fieldObj);
        } else {
            fieldObj.put("ticket", (Object)value);
            jsapiMap.put(agentid, fieldObj);
        }
    }

    public DingtalkProperties.DingtalkAppConfig getAppConfig() {
        return this.appConfig;
    }
}
