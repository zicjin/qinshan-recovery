/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.helper;

import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.sunwayworld.framework.utils.JsonUtils;
import java.util.HashMap;

public class GetuiHelper {
    public static String sendMsg(String clientId, String title, String content, String data) {
        IGtPush push = new IGtPush("http://sdk.open.api.igexin.com/apiex.htm", "vqyiSTwmjVA0mJkvK6XP93", "yjjYpKLZZNAr0pJ4OvoaR6");
        NotificationTemplate template = GetuiHelper.getNotificationTemplate(title, content, data);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        message.setOfflineExpireTime(86400000L);
        message.setStrategyJson("{\"ios\":4}");
        message.setData(template);
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId("QvDUhK1rpW7nERpzNJSHL");
        target.setClientId(clientId);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        }
        catch (RequestException e) {
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (ret != null) {
            if (!"ok".equals(ret.getResponse().get("result"))) {
                return ret.getResponse().toString();
            }
            return "ok";
        }
        return "\u670d\u52a1\u5668\u54cd\u5e94\u5f02\u5e38\uff01";
    }

    private static NotificationTemplate getNotificationTemplate(String title, String content, String data) {
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId("QvDUhK1rpW7nERpzNJSHL");
        template.setAppkey("vqyiSTwmjVA0mJkvK6XP93");
        template.setTransmissionType(1);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("content", content);
        map.put("data", data);
        template.setTransmissionContent(JsonUtils.toJsonString(map));
        template.setAPNInfo(GetuiHelper.getAPNPayload(title, content, data));
        Style0 style = new Style0();
        style.setTitle(title);
        style.setText(content);
        style.setLogo("icon.png");
        style.setLogoUrl("");
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        return template;
    }

    private static APNPayload getAPNPayload(String title, String content, String data) {
        APNPayload payload = new APNPayload();
        payload.setAutoBadge("+1");
        payload.setContentAvailable(0);
        payload.setSound("default");
        payload.addCustomMsg("title", title);
        payload.addCustomMsg("content", content);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", (Object)title);
        jsonObject.put("content", (Object)content);
        jsonObject.put("data", (Object)data);
        jsonObject.put("isread", (Object)false);
        payload.addCustomMsg("payload", jsonObject);
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(content));
        payload.setVoicePlayType(0);
        return payload;
    }
}
