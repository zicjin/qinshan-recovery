/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class ConUserApi {
    private static String createUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=";
    private static String uploadUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=";
    private static String deleteUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/delete";
    private static String batchDeleteUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete?access_token=";
    private static String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
    private static String getDepartmentSimpleListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist";
    private static String getDepartmentListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/list";
    private static String inviteUrl = "https://qyapi.weixin.qq.com/cgi-bin/invite/send?access_token=";

    public static ApiResult createUser(String data) {
        String jsonResult = HttpUtils.post(createUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateUser(String data) {
        String jsonResult = HttpUtils.post(uploadUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteUser(String userid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("userid", userid);
        String jsonResult = HttpUtils.get(deleteUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchDeleteUser(String data) {
        String jsonResult = HttpUtils.post(batchDeleteUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUser(String userid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("userid", userid);
        String jsonResult = HttpUtils.get(getUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getDepartmentUserSimpleList(String department_id, String fetch_child, String status) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("department_id", department_id).put("fetch_child", fetch_child).put("status", status);
        String jsonResult = HttpUtils.get(getDepartmentSimpleListUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getDepartmentUserList(String department_id, String fetch_child, String status) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("department_id", department_id).put("fetch_child", fetch_child).put("status", status);
        String jsonResult = HttpUtils.get(getDepartmentListUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult inviteUser(String data) {
        String jsonResult = HttpUtils.post(inviteUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }
}
