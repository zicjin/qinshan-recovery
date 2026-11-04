/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;

public class OAuthApi {
    private static String getCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=";
    private static String getUserInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=";
    private static String toOpenIdUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=";
    private static String toUserIdUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_userid?access_token=";
    private static String getuserdetailURl = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserdetail?access_token=";

    public static String getAuthorizeURL(String redirectUri, String corpId, String state, String agentId, boolean isUserInfo) {
        String scope = "snsapi_base";
        if (isUserInfo) {
            scope = "snsapi_privateinfo";
        }
        StringBuffer sbf = new StringBuffer();
        sbf.append(getCodeUrl).append(corpId).append("&redirect_uri=").append(redirectUri).append("&response_type=code").append("&agentid=").append(agentId).append("&scope=").append(scope);
        if (!StringUtils.isBlank(state)) {
            sbf.append("&state=").append(state);
        }
        sbf.append("#wechat_redirect");
        return sbf.toString();
    }

    public static ApiResult getUserInfoByCode(String code) {
        String jsonResult = HttpUtils.get(getUserInfoUrl + AccessTokenApi.getAccessTokenStr() + "&code=" + code);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUserInfo(String user_ticket) {
        String jsonResult = HttpUtils.post(getuserdetailURl + AccessTokenApi.getAccessTokenStr(), user_ticket);
        return new ApiResult(jsonResult);
    }

    public static ApiResult ToOpenId(String userid, String agentid) {
        Kv kv = Kv.by("userid", userid).set("agentid", agentid);
        String jsonResult = HttpUtils.post(toOpenIdUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(kv));
        return new ApiResult(jsonResult);
    }

    public static ApiResult ToUserId(String openid) {
        Kv kv = Kv.by("openid", openid);
        String jsonResult = HttpUtils.post(toUserIdUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(kv));
        return new ApiResult(jsonResult);
    }
}
