/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;

public class DatacubeApi {
    private static String getUserSummaryUrl = "https://api.weixin.qq.com/datacube/getusersummary?access_token=";
    private static String getUserCumulateUrl = "https://api.weixin.qq.com/datacube/getusercumulate?access_token=";
    private static String getArticleSummaryUrl = "https://api.weixin.qq.com/datacube/getarticlesummary?access_token=";
    private static String getArticlEtotalURL = "https://api.weixin.qq.com/datacube/getarticletotal?access_token=";
    private static String getUserReadURL = "https://api.weixin.qq.com/datacube/getuserread?access_token=";
    private static String getUserReadHourURL = "https://api.weixin.qq.com/datacube/getuserreadhour?access_token=";
    private static String getUserShareURL = "https://api.weixin.qq.com/datacube/getusershare?access_token=";
    private static String getUserShareHourURL = "https://api.weixin.qq.com/datacube/getusersharehour?access_token=";
    private static String getUpStreamMsgURL = "https://api.weixin.qq.com/datacube/getupstreammsg?access_token=";
    private static String getUpStreamMsgHourURL = "https://api.weixin.qq.com/datacube/getupstreammsghour?access_token=";
    private static String getUpStreamMsgWeekMsgURL = "https://api.weixin.qq.com/datacube/getupstreammsgweek?access_token=";
    private static String getUpStreamMsgMonthURL = "https://api.weixin.qq.com/datacube/getupstreammsgmonth?access_token=";
    private static String getUpStreamMsgDistURL = "https://api.weixin.qq.com/datacube/getupstreammsgdist?access_token=";
    private static String getUpStreamMsgDistWeekURL = "https://api.weixin.qq.com/datacube/getupstreammsgdistweek?access_token=";
    private static String getUpStreamMsgDistMonthURL = "https://api.weixin.qq.com/datacube/getupstreammsgdistmonth?access_token=";
    private static String getInterFaceSummaryURL = "https://api.weixin.qq.com/datacube/getinterfacesummary?access_token=";
    private static String getInterFaceSummaryHourURL = "https://api.weixin.qq.com/datacube/getinterfacesummaryhour?access_token=";
    private static String getCardBizuinInfo = "https://api.weixin.qq.com/datacube/getcardbizuininfo?access_token=";
    private static String getCardInfo = "https://api.weixin.qq.com/datacube/getcardcardinfo?access_token=";
    private static String getMemberCardInfo = "https://api.weixin.qq.com/datacube/getcardmembercardinfo?access_token=";
    private static String getMemberCardDetail = "https://api.weixin.qq.com/datacube/getcardmembercarddetail?access_token=";

    private static ApiResult getData(String url, String begin_date, String end_date) {
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("begin_date", begin_date);
        mapData.put("end_date", end_date);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(mapData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUserSummary(String begin_date, String end_date) {
        String url = getUserSummaryUrl + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUserCumulate(String begin_date, String end_date) {
        String url = getUserCumulateUrl + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getArticleSummary(String begin_date, String end_date) {
        String url = getArticleSummaryUrl + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getArticlEtotal(String begin_date, String end_date) {
        String url = getArticlEtotalURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUserRead(String begin_date, String end_date) {
        String url = getUserReadURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUserReadHour(String begin_date, String end_date) {
        String url = getUserReadHourURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUserShare(String begin_date, String end_date) {
        String url = getUserShareURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUserShareHour(String begin_date, String end_date) {
        String url = getUserShareHourURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsg(String begin_date, String end_date) {
        String url = getUpStreamMsgURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgHour(String begin_date, String end_date) {
        String url = getUpStreamMsgHourURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgWeekMsg(String begin_date, String end_date) {
        String url = getUpStreamMsgWeekMsgURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgMonth(String begin_date, String end_date) {
        String url = getUpStreamMsgMonthURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgDist(String begin_date, String end_date) {
        String url = getUpStreamMsgDistURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgDistWeek(String begin_date, String end_date) {
        String url = getUpStreamMsgDistWeekURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getUpStreamMsgDistMonth(String begin_date, String end_date) {
        String url = getUpStreamMsgDistMonthURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getInterFaceSummary(String begin_date, String end_date) {
        String url = getInterFaceSummaryURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getInterFaceSummaryHour(String begin_date, String end_date) {
        String url = getInterFaceSummaryHourURL + AccessTokenApi.getAccessTokenStr();
        return DatacubeApi.getData(url, begin_date, end_date);
    }

    public static ApiResult getCardBizuinInfo(String beginDate, String endDate, int condSource) {
        String url = getCardBizuinInfo + AccessTokenApi.getAccessTokenStr();
        Kv data = Kv.by("begin_date", beginDate).set("end_date", endDate).set("cond_source", condSource);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getCardInfo(String beginDate, String endDate, int condSource) {
        return DatacubeApi.getCardInfo(beginDate, endDate, condSource, null);
    }

    public static ApiResult getCardInfo(String beginDate, String endDate, int condSource, String cardId) {
        String url = getCardInfo + AccessTokenApi.getAccessTokenStr();
        Kv data = Kv.by("begin_date", beginDate).set("end_date", endDate).set("cond_source", condSource);
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getMemberCardInfo(String beginDate, String endDate, int condSource) {
        String url = getMemberCardInfo + AccessTokenApi.getAccessTokenStr();
        Kv data = Kv.by("begin_date", beginDate).set("end_date", endDate).set("cond_source", condSource);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getMemberCardDetail(String beginDate, String endDate, String cardId) {
        String url = getMemberCardDetail + AccessTokenApi.getAccessTokenStr();
        Kv data = Kv.by("begin_date", beginDate).set("end_date", endDate).set("card_id", cardId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
