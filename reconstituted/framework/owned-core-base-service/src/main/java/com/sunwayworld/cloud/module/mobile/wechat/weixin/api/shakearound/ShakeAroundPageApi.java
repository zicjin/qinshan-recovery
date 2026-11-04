/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;

public class ShakeAroundPageApi {
    private static String pageAddUrl = "https://api.weixin.qq.com/shakearound/page/add?access_token=";
    private static String pageUpdateUrl = "https://api.weixin.qq.com/shakearound/page/update?access_token=";
    private static String pageSearchUrl = "https://api.weixin.qq.com/shakearound/page/search?access_token=";
    private static String pageDeleteUrl = "https://api.weixin.qq.com/shakearound/page/delete?access_token=";

    public static ApiResult addPage(String title, String pageUrl, String description, String iconUrl) {
        return ShakeAroundPageApi.addPage(title, pageUrl, description, null, iconUrl);
    }

    public static ApiResult addPage(String title, String pageUrl, String description, String comment, String iconUrl) {
        String url = pageAddUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("title", title);
        data.put("description", description);
        data.put("page_url", pageUrl);
        if (!StringUtils.isBlank(comment)) {
            data.put("comment", comment);
        }
        data.put("icon_url", iconUrl);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updatePage(int pageId, String title, String pageUrl, String description, String iconUrl) {
        return ShakeAroundPageApi.updatePage(pageId, title, pageUrl, description, null, iconUrl);
    }

    public static ApiResult updatePage(int pageId, String title, String pageUrl, String description, String comment, String iconUrl) {
        String url = pageUpdateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("page_id", pageId);
        data.put("title", title);
        data.put("description", description);
        data.put("page_url", pageUrl);
        if (!StringUtils.isBlank(comment)) {
            data.put("comment", comment);
        }
        data.put("icon_url", iconUrl);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchByIds(int ... pageIds) {
        String url = pageSearchUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("type", 1);
        data.put("page_ids", pageIds);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchPage(int begin, int count) {
        String url = pageSearchUrl + AccessTokenApi.getAccessTokenStr();
        if (begin < 0) {
            begin = 0;
        }
        if (count > 50) {
            count = 50;
        }
        if (count < 1) {
            count = 1;
        }
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("type", 2);
        data.put("begin", begin);
        data.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult deletePage(int pageId) {
        String url = pageDeleteUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("page_id", pageId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
