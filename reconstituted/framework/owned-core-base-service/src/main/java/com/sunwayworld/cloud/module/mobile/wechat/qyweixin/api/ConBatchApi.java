/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class ConBatchApi {
    private static String batchInviteUsersUrl = "https://qyapi.weixin.qq.com/cgi-bin/batch/inviteuser?access_token=ACCESS_TOKEN";
    private static String batchSyncUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/batch/syncuser?access_token=ACCESS_TOKEN";
    private static String batchReplaceUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceuser?access_token=ACCESS_TOKEN";
    private static String batchReplacePartyUrl = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceparty?access_token=ACCESS_TOKEN";
    private static String batchGetResultUrl = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?access_token=ACCESS_TOKEN&jobid=JOBID";

    public static ApiResult inviteUsers(String data) {
        String jsonStr = HttpUtils.post(batchInviteUsersUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()), data);
        return new ApiResult(jsonStr);
    }

    public static ApiResult updateSyncUser(String data) {
        String jsonStr = HttpUtils.post(batchSyncUserUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()), data);
        return new ApiResult(jsonStr);
    }

    public static ApiResult batgReplaceUser(String data) {
        String jsonStr = HttpUtils.post(batchReplaceUserUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()), data);
        return new ApiResult(jsonStr);
    }

    public static ApiResult batchReplaceParty(String data) {
        String jsonStr = HttpUtils.post(batchReplacePartyUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()), data);
        return new ApiResult(jsonStr);
    }

    public static ApiResult batchGetResult(String jobId) {
        String jsonStr = HttpUtils.get(batchGetResultUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()).replace("JOBID", jobId));
        return new ApiResult(jsonStr);
    }
}
