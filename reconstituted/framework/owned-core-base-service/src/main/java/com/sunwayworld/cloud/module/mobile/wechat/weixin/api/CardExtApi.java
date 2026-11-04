/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class CardExtApi {
    private static String meetingTicketUpdateUserUrl = "https://api.weixin.qq.com/card/meetingticket/updateuser?access_token=";
    private static String movieTicketUpdateUserUrl = "https://api.weixin.qq.com/card/movieticket/updateuser?access_token=";
    private static String checkinBoardingpassUrl = "https://api.weixin.qq.com/card/boardingpass/checkin?access_token=";

    public static ApiResult UpdateMeetingTicketUser(String jsonStr) {
        String jsonResult = HttpUtils.post(meetingTicketUpdateUserUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult UpdateMovieTicketUser(String jsonStr) {
        String jsonResult = HttpUtils.post(movieTicketUpdateUserUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult checkinBoardingpass(String jsonStr) {
        String jsonResult = HttpUtils.post(checkinBoardingpassUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }
}
