/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class TemplateMsgApi {
    private static String sendApiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    private static String setIndustryUrl = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=";
    private static String getIndustryUrl = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=";
    private static String getTemplateIdUrl = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=";
    private static String getAllTemplateUrl = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=";
    private static String delTemplateUrl = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=";

    public static ApiResult send(String jsonStr) {
        String jsonResult = HttpUtils.post(sendApiUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult setIndustry(String industry_id1, String industry_id2) {
        String url = setIndustryUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("industry_id1", industry_id1);
        params.put("industry_id2", industry_id2);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getIndustry() {
        return new ApiResult(HttpUtils.get(getIndustryUrl + AccessTokenApi.getAccessTokenStr()));
    }

    public static ApiResult getTemplateId(String templateIdShort) {
        String url = getTemplateIdUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("template_id_short", templateIdShort);
        String json = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(json);
    }

    public static ApiResult getAllTemplate() {
        return new ApiResult(HttpUtils.get(getAllTemplateUrl + AccessTokenApi.getAccessTokenStr()));
    }

    public static ApiResult delTemplateById(String templateId) {
        String url = delTemplateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("template_id", templateId);
        String json = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(json);
    }
}
