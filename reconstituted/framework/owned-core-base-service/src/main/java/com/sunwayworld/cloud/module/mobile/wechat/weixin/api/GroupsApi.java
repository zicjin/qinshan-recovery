/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;
import java.util.List;

public class GroupsApi {
    private static String createUrl = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=";
    private static String getUrl = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=";
    private static String getIdUrl = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=";
    private static String updateUrl = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=";
    private static String membersUpdateUrl = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=";
    private static String membersBatchUpdateUrl = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=";
    private static String deleteUrl = "https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=";

    public static ApiResult create(String name) {
        String url = createUrl + AccessTokenApi.getAccessTokenStr();
        HashMap groupData = new HashMap();
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("name", name);
        groupData.put("group", mapData);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult get() {
        String url = getUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(url);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getId(String openid) {
        String url = getIdUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("openid", openid);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult update(int id, String name) {
        String url = updateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap groupData = new HashMap();
        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("id", id);
        mapData.put("name", name);
        groupData.put("group", mapData);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult membersUpdate(String openid, int to_groupid) {
        String url = membersUpdateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("openid", openid);
        mapData.put("to_groupid", to_groupid);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult membersBatchUpdate(List<String> openidList, int to_groupid) {
        String url = membersBatchUpdateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("openid_list", openidList);
        mapData.put("to_groupid", to_groupid);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult delete(int id) {
        String url = deleteUrl + AccessTokenApi.getAccessTokenStr();
        HashMap groupData = new HashMap();
        HashMap<String, Integer> mapData = new HashMap<String, Integer>();
        mapData.put("id", id);
        groupData.put("group", mapData);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }
}
