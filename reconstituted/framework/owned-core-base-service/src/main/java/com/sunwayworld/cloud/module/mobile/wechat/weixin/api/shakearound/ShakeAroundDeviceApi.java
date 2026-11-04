/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound.bean.DeviceIdentifier;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.List;

public class ShakeAroundDeviceApi {
    private static String applyIdUrl = "https://api.weixin.qq.com/shakearound/device/applyid?access_token=";
    private static String applyStatusUrl = "https://api.weixin.qq.com/shakearound/device/applystatus?access_token=";
    private static String updateUrl = "https://api.weixin.qq.com/shakearound/device/update?access_token=";
    private static String bindLocationUrl = "https://api.weixin.qq.com/shakearound/device/bindlocation?access_token=";
    private static String searchUrl = "https://api.weixin.qq.com/shakearound/device/search?access_token=";
    private static String bindPageUrl = "https://api.weixin.qq.com/shakearound/device/bindpage?access_token=";
    private static String relationSearchUrl = "https://api.weixin.qq.com/shakearound/relation/search?access_token=";
    private static String groupAddUrl = "https://api.weixin.qq.com/shakearound/device/group/add?access_token=";
    private static String groupUpdateUrl = "https://api.weixin.qq.com/shakearound/device/group/update?access_token=";
    private static String groupDeleteUrl = "https://api.weixin.qq.com/shakearound/device/group/delete?access_token=";
    private static String groupGetListUrl = "https://api.weixin.qq.com/shakearound/device/group/getlist?access_token=";
    private static String groupGetDetailUrl = "https://api.weixin.qq.com/shakearound/device/group/getdetail?access_token=";
    private static String groupAddDeviceUrl = "https://api.weixin.qq.com/shakearound/device/group/adddevice?access_token=";
    private static String groupDeleteDeviceUrl = "https://api.weixin.qq.com/shakearound/device/group/deletedevice?access_token=";

    public static ApiResult applyId(int quantity, String applyReason, String comment, Integer poiId) {
        String url = applyIdUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("quantity", quantity);
        data.put("apply_reason", applyReason);
        if (StringUtils.isBlank(comment)) {
            data.put("comment", comment);
        }
        if (null == poiId) {
            data.put("poi_id", poiId);
        }
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getApplyStatus(int applyId) {
        String url = applyStatusUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("apply_id", applyId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateDeviceInfo(DeviceIdentifier deviceIdentifier, String comment) {
        String url = updateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifier", deviceIdentifier);
        data.put("comment", comment);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult bindLocation(DeviceIdentifier deviceIdentifier, String poiId) {
        String url = bindLocationUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifier", deviceIdentifier);
        data.put("poi_id", poiId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchByDevice(DeviceIdentifier deviceIdentifier) {
        String url = searchUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifiers", deviceIdentifier);
        data.put("type", 1);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchPage(int begin, int count) {
        String url = searchUrl + AccessTokenApi.getAccessTokenStr();
        if (begin < 0) {
            begin = 0;
        }
        if (count > 50) {
            count = 50;
        }
        if (count < 1) {
            count = 1;
        }
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("type", 2);
        data.put("begin", begin);
        data.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchPage(int applyId, int begin, int count) {
        String url = searchUrl + AccessTokenApi.getAccessTokenStr();
        if (begin < 0) {
            begin = 0;
        }
        if (count > 50) {
            count = 50;
        }
        if (count < 1) {
            count = 1;
        }
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("type", 3);
        data.put("apply_id", applyId);
        data.put("begin", begin);
        data.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult bindPage(DeviceIdentifier deviceIdentifier, int ... pageIds) {
        String url = bindPageUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifier", deviceIdentifier);
        data.put("page_ids", pageIds);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult searchRelation(DeviceIdentifier deviceIdentifier, int type) {
        String url = relationSearchUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("device_identifier", deviceIdentifier);
        data.put("type", type);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult addGroup(String groupName) {
        String url = groupAddUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> groupData = new HashMap<String, String>();
        groupData.put("group_name", groupName);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateGroup(int groupId, String groupName) {
        String url = groupUpdateUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> groupData = new HashMap<String, Object>();
        groupData.put("group_id", groupId);
        groupData.put("group_name", groupName);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteGroup(int groupId) {
        String url = groupDeleteUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> groupData = new HashMap<String, Integer>();
        groupData.put("group_id", groupId);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getGroupList(int begin, int count) {
        String url = groupGetListUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> groupData = new HashMap<String, Integer>();
        groupData.put("begin", begin);
        groupData.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getGroupDetail(int groupId, int begin, int count) {
        String url = groupGetDetailUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> groupData = new HashMap<String, Integer>();
        groupData.put("group_id", groupId);
        groupData.put("begin", begin);
        groupData.put("count", count);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult addDeviceToGroup(int groupId, List<DeviceIdentifier> deviceIdentifierList) {
        String url = groupAddDeviceUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> groupData = new HashMap<String, Object>();
        groupData.put("group_id", groupId);
        groupData.put("device_identifiers", deviceIdentifierList);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteDeviceFromGroup(int groupId, List<DeviceIdentifier> deviceIdentifierList) {
        String url = groupDeleteDeviceUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> groupData = new HashMap<String, Object>();
        groupData.put("group_id", groupId);
        groupData.put("device_identifiers", deviceIdentifierList);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(groupData));
        return new ApiResult(jsonResult);
    }
}
