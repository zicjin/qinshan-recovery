/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class ConDepartmentApi {
    private static String createUrl = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=";
    private static String uploadUrl = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=";
    private static String deleteUrl = "https://qyapi.weixin.qq.com/cgi-bin/department/delete";
    private static String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/department/list";

    public static ApiResult createDepartment(String data) {
        String jsonResult = HttpUtils.post(createUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateDepartment(String data) {
        String jsonResult = HttpUtils.post(uploadUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteDepartment(String id) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("id", id);
        String jsonResult = HttpUtils.get(deleteUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getDepartment(String id) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("id", id);
        String jsonResult = HttpUtils.get(getUrl, pm.getData());
        return new ApiResult(jsonResult);
    }
}
