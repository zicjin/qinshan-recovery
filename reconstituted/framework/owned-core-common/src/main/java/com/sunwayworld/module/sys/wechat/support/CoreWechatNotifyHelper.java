/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatNoticeDTO;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatNoticeDataDTO;
import com.sunwayworld.module.sys.wechat.support.CoreWechatMPManager;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CoreWechatNotifyHelper {
    private static CoreWechatMPManager coreWechatMPManager;
    private static HttpClientManager httpClinet;

    @Autowired
    public void setCoreWechatMPManager(CoreWechatMPManager coreWechatMPManager) {
        CoreWechatNotifyHelper.coreWechatMPManager = coreWechatMPManager;
    }

    public static void sendNotice(String touser, String templateid, Map<String, String> data) {
        CoreWechatNoticeDTO notice = new CoreWechatNoticeDTO();
        notice.setTouser(touser);
        notice.setTemplate_id(templateid);
        HashMap<String, CoreWechatNoticeDataDTO> finalData = new HashMap<String, CoreWechatNoticeDataDTO>();
        if (data != null) {
            data.entrySet().forEach(entry -> finalData.put((String)entry.getKey(), new CoreWechatNoticeDataDTO((String)entry.getValue())));
        }
        notice.setData(finalData);
        CoreWechatNotifyHelper.sendNotice(JSON.toJSONString(notice));
    }

    public static void sendNotice(String json) {
        String accessToken = coreWechatMPManager.getAccessToken();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com");
        uriComponentsBuilder.path("/cgi-bin/message/template/send").queryParam("access_token", accessToken);
        String result = httpClinet.sendHttpPost(uriComponentsBuilder.toUriString(), json);
        JSONObject jsonObject = JSON.parseObject(result);
        if (!ObjectUtils.equals(jsonObject.get("errcode"), 0)) {
            throw new ApplicationWarningException(result, new String[0]);
        }
    }

    public static void sendCorpNotice(String touser, String agentid, String content) {
        HashMap<String, Object> requestBody = new HashMap<String, Object>();
        HashMap<String, String> text = new HashMap<String, String>();
        requestBody.put("touser", touser);
        text.put("content", content);
        requestBody.put("text", text);
        requestBody.put("agentid", agentid);
        requestBody.put("msgtype", "text");
        CoreWechatNotifyHelper.sendCorpNotice(JSON.toJSONString(requestBody));
    }

    public static void sendCorpNotice(String json) {
        String accessToken = coreWechatMPManager.getCorpAccessToken();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("https://qyapi.weixin.qq.com");
        uriComponentsBuilder.path("/cgi-bin/message/send").queryParam("access_token", accessToken);
        String result = httpClinet.sendHttpPost(uriComponentsBuilder.toUriString(), json);
        JSONObject jsonObject = JSON.parseObject(result);
        if (!ObjectUtils.equals(jsonObject.get("errcode"), 0)) {
            throw new ApplicationWarningException(result, new String[0]);
        }
    }

    static {
        httpClinet = HttpClientManager.getInstance();
    }
}
