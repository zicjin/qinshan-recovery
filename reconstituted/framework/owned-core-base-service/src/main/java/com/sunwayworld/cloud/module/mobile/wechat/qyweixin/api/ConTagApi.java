/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class ConTagApi {
    private static String createUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/create?access_token=";
    private static String uploadUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/update?access_token=";
    private static String deleteUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete";
    private static String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/get";
    private static String addTagUsersUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers?access_token=";
    private static String delTagUsersUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers?access_token=";
    private static String getTagListUrl = "https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=";

    public static ApiResult createTag(String data) {
        String jsonResult = HttpUtils.post(createUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateTag(String data) {
        String jsonResult = HttpUtils.post(uploadUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteTag(String tagid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("tagid", tagid);
        String jsonResult = HttpUtils.get(deleteUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getTagUser(String tagid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("tagid", tagid);
        String jsonResult = HttpUtils.get(getUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult addTagUsers(String data) {
        String jsonResult = HttpUtils.post(addTagUsersUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteTagUsers(String data) {
        String jsonResult = HttpUtils.post(delTagUsersUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getTagList() {
        String jsonResult = HttpUtils.get(getTagListUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}
