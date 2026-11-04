/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.List;

public class TagApi {
    private static String CREATE_URL = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=";
    private static String GET_URL = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=";
    private static String UPDATE_URL = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=";
    private static String DELETE_URL = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=";
    private static String GET_USER_URL = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=";
    private static String BATCH_TAGGING_URL = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=";
    private static String BATCH_UNTAGGING_URL = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=";
    private static String GET_ID_LIST_URL = "https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=";

    public static ApiResult create(String name) {
        String url = CREATE_URL + AccessTokenApi.getAccessTokenStr();
        HashMap data = new HashMap();
        HashMap<String, String> tags = new HashMap<String, String>();
        tags.put("name", name);
        data.put("tag", tags);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult get() {
        String url = GET_URL + AccessTokenApi.getAccessTokenStr();
        return new ApiResult(HttpUtils.get(url));
    }

    public static ApiResult update(int id, String name) {
        String url = UPDATE_URL + AccessTokenApi.getAccessTokenStr();
        HashMap data = new HashMap();
        HashMap<String, Object> tags = new HashMap<String, Object>();
        tags.put("id", id);
        tags.put("name", name);
        data.put("tag", tags);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult delete(int id) {
        String url = DELETE_URL + AccessTokenApi.getAccessTokenStr();
        HashMap data = new HashMap();
        HashMap<String, Integer> tags = new HashMap<String, Integer>();
        tags.put("id", id);
        data.put("tag", tags);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUser(int tagId) {
        return TagApi.getUser(tagId, null);
    }

    public static ApiResult getUser(int tagId, String nextOpenId) {
        String url = GET_USER_URL + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("tagid", tagId);
        if (!StringUtils.isBlank(nextOpenId)) {
            data.put("next_openid", nextOpenId);
        }
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchAddTag(int tagId, List<String> openIdList) {
        String url = BATCH_TAGGING_URL + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("tagid", tagId);
        data.put("openid_list", openIdList);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchDelTag(int tagId, List<String> openIdList) {
        String url = BATCH_UNTAGGING_URL + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("tagid", tagId);
        data.put("openid_list", openIdList);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUser(String openId) {
        String url = GET_ID_LIST_URL + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("openid", openId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
