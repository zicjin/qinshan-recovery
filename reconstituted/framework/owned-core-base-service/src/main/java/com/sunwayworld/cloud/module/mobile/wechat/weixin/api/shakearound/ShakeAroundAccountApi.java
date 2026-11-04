/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;

public class ShakeAroundAccountApi {
    private static String registerUrl = "https://api.weixin.qq.com/shakearound/account/register?access_token=";
    private static String auditStatusUrl = "https://api.weixin.qq.com/shakearound/account/auditstatus?access_token=";

    public static ApiResult register(String name, String phoneNumber, String email, String industryId, String applyReason, String ... qualificationCertUrls) {
        String url = registerUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("phone_number", phoneNumber);
        data.put("email", email);
        data.put("industry_id", industryId);
        if (!StringUtils.isBlank(applyReason)) {
            data.put("apply_reason", applyReason);
        }
        data.put("qualification_cert_urls", qualificationCertUrls);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getAuditStatus() {
        String url = auditStatusUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(url);
        return new ApiResult(jsonResult);
    }
}
