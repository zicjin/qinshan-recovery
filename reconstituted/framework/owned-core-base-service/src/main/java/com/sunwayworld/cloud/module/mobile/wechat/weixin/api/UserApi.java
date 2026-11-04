/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserApi {
    private static String getUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info";
    private static String getFollowers = "https://api.weixin.qq.com/cgi-bin/user/get";
    private static String batchGetUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=";
    private static String updateRemarkUrl = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=";

    public static ApiResult getUserInfo(String openId) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("openid", openId).put("lang", "zh_CN");
        return new ApiResult(HttpUtils.get(getUserInfo, pm.getData()));
    }

    public static ApiResult getFollowers(String nextOpenid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr());
        if (nextOpenid != null) {
            pm.put("next_openid", nextOpenid);
        }
        return new ApiResult(HttpUtils.get(getFollowers, pm.getData()));
    }

    public static ApiResult getFollows() {
        return UserApi.getFollowers(null);
    }

    public static ApiResult batchGetUserInfo(String jsonStr) {
        String jsonResult = HttpUtils.post(batchGetUserInfo + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchGetUserInfo(List<String> openIdList) {
        HashMap userListMap = new HashMap();
        ArrayList userList = new ArrayList();
        for (String openId : openIdList) {
            HashMap<String, String> mapData = new HashMap<String, String>();
            mapData.put("openid", openId);
            mapData.put("lang", "zh_CN");
            userList.add(mapData);
        }
        userListMap.put("user_list", userList);
        return UserApi.batchGetUserInfo(JSON.toJSONString(userListMap));
    }

    public static ApiResult updateRemark(String openid, String remark) {
        String url = updateRemarkUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("openid", openid);
        mapData.put("remark", remark);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }
}
