/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.xxpt.gateway.shared.api.request.OapiGetJsapiTokenJsonRequest;
import com.alibaba.xxpt.gateway.shared.api.request.OapiRpcOauth2DingtalkAppUserJsonRequest;
import com.alibaba.xxpt.gateway.shared.api.response.OapiGetJsapiTokenJsonResponse;
import com.alibaba.xxpt.gateway.shared.api.response.OapiRpcOauth2DingtalkAppUserJsonResponse;
import com.alibaba.xxpt.gateway.shared.client.http.ExecutableClient;
import com.alibaba.xxpt.gateway.shared.client.http.GetClient;
import com.alibaba.xxpt.gateway.shared.client.http.IntelligentGetClient;
import com.alibaba.xxpt.gateway.shared.client.http.IntelligentPostClient;
import com.sunwayworld.cloud.module.mobile.zwdd.config.ZwddProperties;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.domain.ServiceResult;
import java.util.HashMap;

public class ZwddConfigStore {
    private static HashMap<String, JSONObject> tokenMap = new HashMap();
    private static HashMap<String, JSONObject> jsapiMap = new HashMap();
    private ExecutableClient executableClient;
    private static final long CACHE_TTL = 6600000L;
    private ZwddProperties.ZwddAppConfig appConfig;

    private void initClient() {
        if (this.executableClient == null) {
            this.executableClient = ExecutableClient.getInstance();
            this.executableClient.setDomainName("openplatform.dg-work.cn");
            this.executableClient.setProtocal("https");
            this.executableClient.setAccessKey(this.appConfig.getAppKey());
            this.executableClient.setSecretKey(this.appConfig.getAppSecret());
            this.executableClient.init();
        }
    }

    public ZwddConfigStore(ZwddProperties.ZwddAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public ServiceResult<String> getAccessToken(String agentid) {
        String accessToken = this.getFromCache("accessToken", agentid);
        if (accessToken != null) {
            return ServiceResult.success(accessToken);
        }
        this.initClient();
        GetClient getClient = this.executableClient.newGetClient("/gettoken.json");
        getClient.addParameter("appkey", this.appConfig.getAppKey());
        getClient.addParameter("appsecret", this.appConfig.getAppSecret());
        String apiResult = getClient.get();
        JSONObject json = JSONObject.parseObject(apiResult);
        if (json.getBoolean("success").booleanValue()) {
            JSONObject contentObj = json.getJSONObject("content");
            accessToken = contentObj.getJSONObject("data").getString("accessToken");
        }
        this.putToCache("accessToken", agentid, accessToken);
        return ServiceResult.success(accessToken);
    }

    public ServiceResult<String> getJsTicket(String agentid) {
        String ticket = this.getFromCache("jsticket", agentid);
        if (ticket != null) {
            return ServiceResult.success(ticket);
        }
        this.initClient();
        ServiceResult<String> tokenSr = this.getAccessToken(agentid);
        if (!tokenSr.isSuccess()) {
            return ServiceResult.failure(tokenSr.getCode(), tokenSr.getMessage());
        }
        String accessToken = tokenSr.getResult();
        IntelligentGetClient intelligentGetClient = this.executableClient.newIntelligentGetClient("/get_jsapi_token.json");
        OapiGetJsapiTokenJsonRequest oapiGetJsapiTokenJsonRequest = new OapiGetJsapiTokenJsonRequest();
        oapiGetJsapiTokenJsonRequest.setAccessToken(accessToken);
        OapiGetJsapiTokenJsonResponse apiResult = intelligentGetClient.get(oapiGetJsapiTokenJsonRequest);
        if (apiResult.getSuccess().booleanValue()) {
            String ticketStr = apiResult.getContent().getData();
            JSONObject ticketJson = JSONObject.parseObject(ticketStr);
            ticket = ticketJson.getString("accessToken");
            this.putToCache("jsticket", agentid, ticket);
            return ServiceResult.success(ticket);
        }
        return ServiceResult.failure(apiResult.getCode(), apiResult.getMessage());
    }

    public ServiceResult<String> getUserInfoByCode(String agentid, String code) {
        this.initClient();
        ServiceResult<String> tokenSr = this.getAccessToken(agentid);
        if (!tokenSr.isSuccess()) {
            return ServiceResult.failure(tokenSr.getCode(), tokenSr.getMessage());
        }
        String accessToken = tokenSr.getResult();
        IntelligentPostClient intelligentPostClient = this.executableClient.newIntelligentPostClient("/rpc/oauth2/dingtalk_app_user.json");
        OapiRpcOauth2DingtalkAppUserJsonRequest oapiRpcOauth2DingtalkAppUserJsonRequest = new OapiRpcOauth2DingtalkAppUserJsonRequest();
        oapiRpcOauth2DingtalkAppUserJsonRequest.setAccess_token(accessToken);
        oapiRpcOauth2DingtalkAppUserJsonRequest.setAuth_code(code);
        OapiRpcOauth2DingtalkAppUserJsonResponse apiResult = intelligentPostClient.post(oapiRpcOauth2DingtalkAppUserJsonRequest);
        JSONObject contentObj = JSONObject.parseObject(apiResult.getContent());
        if (contentObj.getBoolean("success").booleanValue()) {
            JSONObject innerContentObj = contentObj.getJSONObject("content");
            if (innerContentObj.getBoolean("success").booleanValue()) {
                return ServiceResult.success(innerContentObj.getJSONObject("data").toJSONString());
            }
            return ServiceResult.failure("-1", innerContentObj.getString("responseMessage"));
        }
        return ServiceResult.failure(apiResult.getCode(), apiResult.getMessage());
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

    public ZwddProperties.ZwddAppConfig getAppConfig() {
        return this.appConfig;
    }
}
