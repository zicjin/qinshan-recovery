/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.bean.Poi;
import java.util.HashMap;

public class PoiApi {
    private static String addPoi = "http://api.weixin.qq.com/cgi-bin/poi/addpoi?access_token=";
    private static String getPoi = "http://api.weixin.qq.com/cgi-bin/poi/getpoi?access_token=";
    private static String getPoiList = "https://api.weixin.qq.com/cgi-bin/poi/getpoilist?access_token=";
    private static String updatePoi = "https://api.weixin.qq.com/cgi-bin/poi/updatepoi?access_token=";
    private static String delPoi = "https://api.weixin.qq.com/cgi-bin/poi/delpoi?access_token=";
    private static String getWxCategory = "http://api.weixin.qq.com/cgi-bin/poi/getwxcategory";

    public static ApiResult addPoi(String jsonStr) {
        String jsonResult = HttpUtils.post(addPoi + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult addPoi(Poi poi) {
        HashMap business = new HashMap();
        HashMap<String, Poi> base_info = new HashMap<String, Poi>();
        base_info.put("base_info", poi);
        business.put("business", base_info);
        return PoiApi.addPoi(JSON.toJSONString(business));
    }

    public static ApiResult getPoi(String poiId) {
        String url = getPoi + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> poi_id = new HashMap<String, String>();
        poi_id.put("poi_id", poiId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(poi_id));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getPoiList(int begin, int limit) {
        String url = getPoiList + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> poiListPara = new HashMap<String, Integer>();
        poiListPara.put("begin", begin);
        poiListPara.put("limit", limit);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(poiListPara));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updatePoi(String jsonStr) {
        String jsonResult = HttpUtils.post(updatePoi + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult updatePoi(Poi poi) {
        HashMap business = new HashMap();
        HashMap<String, Poi> base_info = new HashMap<String, Poi>();
        base_info.put("base_info", poi);
        business.put("business", base_info);
        return PoiApi.addPoi(JSON.toJSONString(business));
    }

    public static ApiResult delPoi(String poiId) {
        String url = delPoi + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> poi_id = new HashMap<String, String>();
        poi_id.put("poi_id", poiId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(poi_id));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getCategory() {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.get(getWxCategory, pm.getData()));
    }
}
