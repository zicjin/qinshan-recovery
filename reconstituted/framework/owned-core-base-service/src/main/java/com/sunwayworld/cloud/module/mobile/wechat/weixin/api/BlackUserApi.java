/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlackUserApi {
    private static String getBlackList = "https://api.weixin.qq.com/cgi-bin/tags/members/getblacklist?access_token=";
    private static String batchBlackList = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=";
    private static String batchUnblackList = "https://api.weixin.qq.com/cgi-bin/tags/members/batchunblacklist?access_token=";

    public static ApiResult getBlackList(String beginOpenid) {
        String url = getBlackList + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> mapData = new HashMap<String, String>();
        if (!StringUtils.isBlank(beginOpenid)) {
            mapData.put("begin_openid", beginOpenid);
        }
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getBlackList() {
        return BlackUserApi.getBlackList(null);
    }

    public static ApiResult batchBlackUsers(String jsonStr) {
        String jsonResult = HttpUtils.post(batchBlackList + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchBlackUsers(List<String> openIdList) throws IllegalArgumentException {
        if (openIdList == null) {
            throw new IllegalArgumentException();
        }
        HashMap userListMap = new HashMap();
        ArrayList<String> userList = new ArrayList<String>();
        if (openIdList != null && openIdList.size() > 0) {
            for (String openId : openIdList) {
                userList.add(openId);
            }
        }
        userListMap.put("opened_list", userList);
        return BlackUserApi.batchBlackUsers(JSON.toJSONString(userListMap));
    }

    public static ApiResult batchUnblackUsers(String jsonStr) {
        String jsonResult = HttpUtils.post(batchUnblackList + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult batchUnblackUsers(List<String> openIdList) throws IllegalArgumentException {
        if (openIdList == null) {
            throw new IllegalArgumentException();
        }
        HashMap userListMap = new HashMap();
        ArrayList<String> userList = new ArrayList<String>();
        if (openIdList != null && openIdList.size() > 0) {
            for (String openId : openIdList) {
                userList.add(openId);
            }
        }
        userListMap.put("opened_list", userList);
        return BlackUserApi.batchUnblackUsers(JSON.toJSONString(userListMap));
    }
}
