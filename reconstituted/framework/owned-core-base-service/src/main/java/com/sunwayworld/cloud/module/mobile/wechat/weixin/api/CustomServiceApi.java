/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomServiceApi {
    private static String getRecordUrl = "https://api.weixin.qq.com/customservice/msgrecord/getrecord?access_token=";
    private static String addKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=";
    private static String updateKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=";
    private static String delKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/del";
    private static String uploadKfAccountHeadImgUrl = "https://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=";
    private static String getKfListUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=";
    private static String getOnlineKFListUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=";
    private static String customMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    private static String inviteWorkerUrl = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=";
    private static String createSession = "https://api.weixin.qq.com/customservice/kfsession/create?access_token=";
    private static String closeSession = "https://api.weixin.qq.com/customservice/kfsession/close?access_token=";
    private static String getSession = "https://api.weixin.qq.com/customservice/kfsession/getsession";
    private static String getSessionList = "https://api.weixin.qq.com/customservice/kfsession/getsessionlist";
    private static String getWaitCase = "https://api.weixin.qq.com/customservice/kfsession/getwaitcase";
    private static String getMsgList = "https://api.weixin.qq.com/customservice/msgrecord/getmsglist?access_token=";

    public static ApiResult getRecord(String jsonStr) {
        String jsonResult = HttpUtils.post(getRecordUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getRecord(int pageindex, int pagesize, long starttime, long endtime) {
        if (pageindex < 1) {
            pageindex = 1;
        }
        if (pagesize < 1 || pagesize > 50) {
            pagesize = 50;
        }
        HashMap<String, Number> params = new HashMap<String, Number>();
        params.put("pageindex", pageindex);
        params.put("pagesize", pagesize);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        return CustomServiceApi.getRecord(JSON.toJSONString(params));
    }

    public static ApiResult addKfAccount(String kf_account, String nickname, String password) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("kf_account", kf_account);
        params.put("nickname", nickname);
        params.put("password", password);
        String jsonResult = HttpUtils.post(addKfAccountUrl + accessToken, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateKfAccount(String kf_account, String nickname, String password) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("kf_account", kf_account);
        params.put("nickname", nickname);
        params.put("password", password);
        String jsonResult = HttpUtils.post(updateKfAccountUrl + accessToken, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult delKfAccount(String kf_account) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(delKfAccountUrl, ParaMap.create("access_token", accessToken).put("kf_account", kf_account).getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult uploadKfAccountHeadImg(String kf_account, File headImg) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String url = uploadKfAccountHeadImgUrl + accessToken + "&kf_account=" + kf_account;
        String jsonResult = HttpUtils.upload(url, headImg, null);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getKfList() {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getKfListUrl + accessToken);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getOnlineKFList() {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getOnlineKFListUrl + accessToken);
        return new ApiResult(jsonResult);
    }

    private static ApiResult sendMsg(Map<String, Object> message) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(customMessageUrl + accessToken, JSON.toJSONString(message));
        return new ApiResult(jsonResult);
    }

    public static ApiResult sendText(String openId, String text) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "text");
        HashMap<String, String> textObj = new HashMap<String, String>();
        textObj.put("content", text);
        json.put("text", textObj);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendImage(String openId, String media_id) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "image");
        HashMap<String, String> image = new HashMap<String, String>();
        image.put("media_id", media_id);
        json.put("image", image);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendVoice(String openId, String media_id) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "voice");
        HashMap<String, String> voice = new HashMap<String, String>();
        voice.put("media_id", media_id);
        json.put("voice", voice);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendVideo(String openId, String media_id, String title, String description) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "video");
        HashMap<String, String> video = new HashMap<String, String>();
        video.put("media_id", media_id);
        video.put("title", title);
        video.put("description", description);
        json.put("video", video);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendMusic(String openId, String musicurl, String hqmusicurl, String thumb_media_id, String title, String description) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "music");
        HashMap<String, String> music = new HashMap<String, String>();
        music.put("musicurl", musicurl);
        music.put("hqmusicurl", hqmusicurl);
        music.put("thumb_media_id", thumb_media_id);
        music.put("title", title);
        music.put("description", description);
        json.put("music", music);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendNews(String openId, List<Articles> articles) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "news");
        HashMap<String, List<Articles>> news = new HashMap<String, List<Articles>>();
        news.put("articles", articles);
        json.put("news", news);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendMpNews(String openId, String mediaId) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "mpnews");
        HashMap<String, String> news = new HashMap<String, String>();
        news.put("media_id", mediaId);
        json.put("mpnews", news);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult sendCoupon(String openId, String card_id, String card_ext) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "wxcard");
        HashMap<String, String> wxcard = new HashMap<String, String>();
        wxcard.put("card_id", card_id);
        wxcard.put("card_ext", card_ext);
        json.put("wxcard", wxcard);
        return CustomServiceApi.sendMsg(json);
    }

    public static ApiResult inviteWorker(String kf_account, String invite_wx) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("kf_account", kf_account);
        params.put("invite_wx", invite_wx);
        String jsonResult = HttpUtils.post(inviteWorkerUrl + accessToken, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult createSession(String kf_account, String openid) {
        String url = createSession + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("kf_account", kf_account);
        params.put("openid", openid);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult closeSession(String kf_account, String openid) {
        String url = closeSession + AccessTokenApi.getAccessTokenStr();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("kf_account", kf_account);
        params.put("openid", openid);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getSession(String openid) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getSession, ParaMap.create("access_token", accessToken).put("openid", openid).getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getSessionList(String kf_account) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getSessionList, ParaMap.create("access_token", accessToken).put("kf_account", kf_account).getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getWaitCase() {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getWaitCase, ParaMap.create("access_token", accessToken).getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getMsgList(int starttime, int endtime, int msgid, int number) {
        String url = getMsgList + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Integer> params = new HashMap<String, Integer>();
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("msgid", msgid);
        params.put("number", number);
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static class Articles {
        private String title;
        private String description;
        private String url;
        private String picurl;

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return this.picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
