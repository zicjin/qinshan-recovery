/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.xxpt.gateway.shared.api.request.OapiMessageWorkNotificationRequest;
import com.alibaba.xxpt.gateway.shared.api.response.OapiMessageWorkNotificationResponse;
import com.alibaba.xxpt.gateway.shared.client.http.ExecutableClient;
import com.alibaba.xxpt.gateway.shared.client.http.IntelligentGetClient;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiWorkrecordUpdateResponse;
import com.sunwayworld.cloud.module.mobile.zwdd.config.ZwddConfiguration;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.BasePushResult;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.ZwddConfigStore;
import com.sunwayworld.framework.utils.StringUtils;
import com.taobao.api.ApiException;

public class ZwddMessageUtil {
    public static BasePushResult sendTextMsg(String agentId, String message, String touser, String toparty, String totag) {
        ZwddConfigStore dingtalkService = ZwddConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(touser);
        request.setAgentId(Long.parseLong(agentId.toString()));
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(message);
        request.setMsg(msg);
        BasePushResult result = new BasePushResult();
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, dingtalkService.getAccessToken(agentId).getResult());
            if (response.getErrcode() == 0L) {
                result.setState(1);
            } else {
                result.setState(-1);
                result.setErrorMsg(response.getErrmsg());
            }
        }
        catch (ApiException e) {
            e.printStackTrace();
            result.setState(-1);
            if (StringUtils.isEmpty(e.getLocalizedMessage())) {
                result.setErrorMsg("Unknow error");
            }
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }

    public static BasePushResult sendImageMsg(String agentId, String mediaId, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendFileMsg(String agentId, String mediaId, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendTextCardMsg(String agentId, String title, String description, String url, String btntxt, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendLinkWorkRecordTextCardMsg(String agentId, String title, String text, String url, String accountid, String tenantid) {
        ZwddConfigStore dingtalkService = ZwddConfiguration.getDtService(agentId);
        String recordId = StringUtils.randomUUID(16);
        String finalUrl = "";
        finalUrl = url.contains("?") ? url + "&ddRecordId=" + recordId : url + "?ddRecordId=" + recordId;
        ExecutableClient executableClient = ExecutableClient.getInstance();
        executableClient.setDomainName("openplatform.dg-work.cn");
        executableClient.setProtocal("https");
        executableClient.setAccessKey(dingtalkService.getAppConfig().getAppKey());
        executableClient.setSecretKey(dingtalkService.getAppConfig().getAppSecret());
        executableClient.init();
        IntelligentGetClient intelligentGetClient = executableClient.newIntelligentGetClient("/message/workNotification");
        OapiMessageWorkNotificationRequest oapiMessageWorkNotificationRequest = new OapiMessageWorkNotificationRequest();
        oapiMessageWorkNotificationRequest.setReceiverIds(accountid);
        oapiMessageWorkNotificationRequest.setTenantId(tenantid);
        oapiMessageWorkNotificationRequest.setBizMsgId(recordId);
        JSONObject linkObj = new JSONObject();
        linkObj.put("messageUrl", (Object)finalUrl);
        linkObj.put("picUrl", (Object)"");
        linkObj.put("title", (Object)title);
        linkObj.put("text", (Object)text);
        JSONObject msgObj = new JSONObject();
        msgObj.put("msgtype", (Object)"link");
        msgObj.put("link", (Object)linkObj);
        oapiMessageWorkNotificationRequest.setMsg(msgObj.toJSONString());
        OapiMessageWorkNotificationResponse apiResult = intelligentGetClient.get(oapiMessageWorkNotificationRequest);
        BasePushResult result = new BasePushResult();
        result.setState(99);
        System.out.println(apiResult.toString());
        System.out.println(apiResult.getContent());
        return result;
    }

    public static BasePushResult completeWorkRecordTextCardMsg(String agentId, String userId, String recordId) {
        ZwddConfigStore dingtalkService = ZwddConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/workrecord/update");
        OapiWorkrecordUpdateRequest req = new OapiWorkrecordUpdateRequest();
        req.setUserid(userId);
        req.setRecordId(recordId);
        BasePushResult result = new BasePushResult();
        try {
            OapiWorkrecordUpdateResponse rsp = client.execute(req, dingtalkService.getAccessToken(agentId).getResult());
            if (rsp.getErrorCode().equals("0")) {
                result.setState(1);
            } else {
                result.setState(-1);
                result.setErrorMsg(rsp.getErrmsg());
            }
        }
        catch (ApiException e) {
            result.setState(-1);
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }
}
