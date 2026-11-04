/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound.bean.DeviceIdentifier;
import java.util.Date;
import java.util.HashMap;

public class ShakeAroundStatisticsApi {
    private static String statisticsDeviceUrl = "https://api.weixin.qq.com/shakearound/statistics/device?access_token=";
    private static String statisticsDeviceListUrl = "https://api.weixin.qq.com/shakearound/statistics/devicelist?access_token=";
    private static String statisticsPageUrl = "https://api.weixin.qq.com/shakearound/statistics/page?access_token=";
    private static String statisticsPageListUrl = "https://api.weixin.qq.com/shakearound/statistics/pagelist?access_token=";

    public static ApiResult getByDevice(DeviceIdentifier deviceIdentifier, Date beginDate, Date endDate) {
        String url = statisticsDeviceUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifier", deviceIdentifier);
        data.put("begin_date", beginDate.getTime() / 1000L);
        data.put("end_date", endDate.getTime() / 1000L);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getDeviceList(Date date, int pageIndex) {
        String url = statisticsDeviceListUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Number> data = new HashMap<String, Number>();
        data.put("date", date.getTime() / 1000L);
        data.put("page_index", pageIndex);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getByPage(int pageId, Date beginDate, Date endDate) {
        String url = statisticsPageUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Number> data = new HashMap<String, Number>();
        data.put("page_id", pageId);
        data.put("begin_date", beginDate.getTime() / 1000L);
        data.put("end_date", endDate.getTime() / 1000L);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getPageList(Date date, int pageIndex) {
        String url = statisticsPageListUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Number> data = new HashMap<String, Number>();
        data.put("date", date.getTime() / 1000L);
        data.put("page_index", pageIndex);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
