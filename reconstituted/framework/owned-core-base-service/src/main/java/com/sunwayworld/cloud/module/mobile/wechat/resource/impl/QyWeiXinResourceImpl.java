/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.resource.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.JsTicket;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeTextCardMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HashKit;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfigKit;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ConUserApi;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.JsTicketApi;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.OAuthApi;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.SendMessageApi;
import com.sunwayworld.cloud.module.mobile.wechat.resource.QyWeiXinResource;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatUserBean;
import com.sunwayworld.module.sys.wechat.service.CoreWechatUserService;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f01\u4e1a\u5fae\u4fe1\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/wechat/qyweixin"})
public class QyWeiXinResourceImpl
implements QyWeiXinResource {
    @Autowired
    private CoreWechatUserService wechatUserService;

    @Override
    public String getJsSign(String requestUrl) {
        JsTicket jsApiTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
        String jsapi_ticket = jsApiTicket.getTicket();
        String nonce_str = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000L);
        String url = "";
        if (!StringUtils.isEmpty(requestUrl) && (url = requestUrl).contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        String signature = HashKit.sha1(str);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("signature", signature);
        data.put("appId", ApiConfigKit.getApiConfig().getCorpId());
        data.put("nonceStr", nonce_str);
        data.put("timestamp", timestamp);
        data.put("jsapi_ticket", jsapi_ticket);
        return JSON.toJSONString(data);
    }

    @Override
    public String getOpenId(String code) {
        ApiResult apiResult = OAuthApi.getUserInfoByCode(code);
        return apiResult.getJson();
    }

    @Override
    public String getWxUser(String wxcode) {
        ApiResult apiResult = ConUserApi.getUser(wxcode);
        return apiResult.getJson();
    }

    @Override
    public String sendMsg(String userId, String title, String description, String url, String btntxt) {
        QiYeTextCardMsg msg = new QiYeTextCardMsg(title, description, url, btntxt);
        CoreWechatUserBean filter = new CoreWechatUserBean();
        filter.setId(userId);
        List<CoreWechatUserBean> wechatUserList = this.wechatUserService.getDao().selectList((CoreWechatUserBean)filter, new Order[0]);
        if (wechatUserList == null || wechatUserList.size() < 1) {
            return "error";
        }
        CoreWechatUserBean wechatUserBean = wechatUserList.get(0);
        msg.setTouser(wechatUserBean.getOpenId());
        msg.setAgentid(ApiConfigKit.getApiConfig().getAgentId());
        ApiResult apiResult = SendMessageApi.sendTextCardMsg(msg);
        return apiResult.getJson();
    }
}
