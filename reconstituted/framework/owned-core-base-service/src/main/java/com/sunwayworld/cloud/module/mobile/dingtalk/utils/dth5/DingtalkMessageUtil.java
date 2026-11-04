/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiWorkrecordAddResponse;
import com.dingtalk.api.response.OapiWorkrecordUpdateResponse;
import com.sunwayworld.cloud.module.mobile.dingtalk.config.DingtalkConfiguration;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.NotificationMsgType;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.BasePushResult;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.DingtalkConfigStore;
import com.sunwayworld.framework.utils.StringUtils;
import com.taobao.api.ApiException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DingtalkMessageUtil {
    public static BasePushResult sendTextMsg(Integer agentId, String message, String touser, String toparty, String totag) {
        DingtalkConfigStore dingtalkService = DingtalkConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(touser);
        request.setAgentId(Long.parseLong(agentId.toString()));
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(message);
        request.setMsg(msg);
        BasePushResult result = new BasePushResult();
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, dingtalkService.getAccessToken().getResult());
            if (response.getErrcode() == 0L) {
                result.setState(1);
            } else {
                result.setState(-1);
                result.setErrorMsg(response.getErrmsg());
            }
        }
        catch (ApiException e) {
            e.printStackTrace();
            result.setState(-1);
            if (StringUtils.isEmpty(e.getLocalizedMessage())) {
                result.setErrorMsg("Unknow error");
            }
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }

    public static BasePushResult sendImageMsg(Integer agentId, String mediaId, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendFileMsg(Integer agentId, String mediaId, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendTextCardMsg(Integer agentId, String title, String description, String url, String btntxt, String touser, String toparty, String totag) {
        return null;
    }

    public static BasePushResult sendWorkRecordTextCardMsg(Integer agentId, String title, List<String[]> descriptions, String url, String btntxt, String touser, String toparty, String totag) {
        String recordId = StringUtils.randomUUID(16);
        DingtalkConfigStore dingtalkService = DingtalkConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/workrecord/add");
        OapiWorkrecordAddRequest req = new OapiWorkrecordAddRequest();
        req.setUserid(touser);
        req.setCreateTime(new Date().getTime());
        req.setTitle(title);
        String finalUrl = "";
        finalUrl = url.contains("?") ? url + "&ddRecordId=" + recordId : url + "?ddRecordId=" + recordId;
        req.setUrl(finalUrl);
        req.setBizId(recordId);
        ArrayList<OapiWorkrecordAddRequest.FormItemVo> formItemVoList = new ArrayList<OapiWorkrecordAddRequest.FormItemVo>();
        for (String[] formitem : descriptions) {
            OapiWorkrecordAddRequest.FormItemVo formItemVo = new OapiWorkrecordAddRequest.FormItemVo();
            formItemVo.setTitle(formitem[0]);
            formItemVo.setContent(formitem[1]);
            formItemVoList.add(formItemVo);
        }
        req.setFormItemList(formItemVoList);
        BasePushResult result = new BasePushResult();
        try {
            OapiWorkrecordAddResponse rsp = client.execute(req, dingtalkService.getAccessToken().getResult());
            if (rsp.getErrorCode().equals("0")) {
                result.setState(1);
                result.setMoreMsg(rsp.getRecordId());
            } else {
                result.setState(-1);
                result.setErrorMsg(rsp.getErrmsg());
            }
        }
        catch (ApiException e) {
            result.setState(-1);
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }

    public static BasePushResult completeWorkRecordTextCardMsg(Integer agentId, String userId, String recordId) {
        DingtalkConfigStore dingtalkService = DingtalkConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/workrecord/update");
        OapiWorkrecordUpdateRequest req = new OapiWorkrecordUpdateRequest();
        req.setUserid(userId);
        req.setRecordId(recordId);
        BasePushResult result = new BasePushResult();
        try {
            OapiWorkrecordUpdateResponse rsp = client.execute(req, dingtalkService.getAccessToken().getResult());
            if (rsp.getErrorCode().equals("0")) {
                result.setState(1);
            } else {
                result.setState(-1);
                result.setErrorMsg(rsp.getErrmsg());
            }
        }
        catch (ApiException e) {
            result.setState(-1);
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }

    public static BasePushResult sendNotificationMsg(Integer agentId, String userIds, NotificationMsgType notificationMsgType, JSONObject msgParam) {
        DingtalkConfigStore dingtalkService = DingtalkConfiguration.getDtService(agentId.toString());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        req.setAgentId((long)agentId);
        req.setUseridList(userIds);
        req.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        switch (notificationMsgType) {
            case TEXT: {
                msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
                msg.getText().setContent(msgParam.getString("content"));
                break;
            }
            case IMAGE: {
                msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
                msg.getImage().setMediaId(msgParam.getString("media_id"));
                break;
            }
            case VOICE: {
                msg.setVoice(new OapiMessageCorpconversationAsyncsendV2Request.Voice());
                msg.getVoice().setMediaId(msgParam.getString("media_id"));
                msg.getVoice().setDuration(msgParam.getString("duration"));
                break;
            }
            case FILE: {
                msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
                msg.getFile().setMediaId(msgParam.getString("media_id"));
                break;
            }
            case LINK: {
                msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
                msg.getLink().setMessageUrl(msgParam.getString("messageUrl"));
                msg.getLink().setTitle(msgParam.getString("title"));
                msg.getLink().setText(msgParam.getString("text"));
                msg.getLink().setPicUrl(msgParam.getString("picUrl"));
                break;
            }
            case OA: {
                msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
                msg.getOa().setMessageUrl(msgParam.getString("message_url"));
                if (msgParam.containsKey("head") && msgParam.getJSONObject("head").containsKey("text")) {
                    msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
                    msg.getOa().getHead().setText(msgParam.getJSONObject("head").getString("text"));
                    msg.getOa().getHead().setBgcolor(msgParam.getJSONObject("head").getString("bgcolor"));
                }
                if (!msgParam.containsKey("body")) break;
                msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
                msg.getOa().getBody().setContent(msgParam.getJSONObject("body").getString("content"));
                msg.getOa().getBody().setAuthor(msgParam.getJSONObject("body").getString("author"));
                msg.getOa().getBody().setTitle(msgParam.getJSONObject("body").getString("title"));
                msg.getOa().getBody().setFileCount(msgParam.getJSONObject("body").getString("file_count"));
                if (msgParam.getJSONObject("body").containsKey("rich")) {
                    msg.getOa().getBody().setRich(new OapiMessageCorpconversationAsyncsendV2Request.Rich());
                    msg.getOa().getBody().getRich().setNum(msgParam.getJSONObject("body").getJSONObject("rich").getString("num"));
                    msg.getOa().getBody().getRich().setNum(msgParam.getJSONObject("body").getJSONObject("rich").getString("num"));
                }
                msg.getOa().getBody().setImage(msgParam.getJSONObject("body").getString("image"));
                if (!msgParam.getJSONObject("body").containsKey("form")) break;
                JSONArray formArr = msgParam.getJSONObject("body").getJSONArray("form");
                ArrayList<OapiMessageCorpconversationAsyncsendV2Request.Form> formList = new ArrayList<OapiMessageCorpconversationAsyncsendV2Request.Form>();
                for (int index = 0; index < formArr.size(); ++index) {
                    JSONObject formItem = formArr.getJSONObject(index);
                    OapiMessageCorpconversationAsyncsendV2Request.Form form = new OapiMessageCorpconversationAsyncsendV2Request.Form();
                    form.setKey(formItem.getString("key"));
                    form.setValue(formItem.getString("value"));
                    formList.add(form);
                }
                msg.getOa().getBody().setForm(formList);
                break;
            }
            case MARKDOWN: {
                msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
                msg.getMarkdown().setText(msgParam.getString("text"));
                msg.getMarkdown().setTitle(msgParam.getString("title"));
                break;
            }
            case ACTIONCARD: {
                msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
                msg.getActionCard().setTitle(msgParam.getString("title"));
                msg.getActionCard().setMarkdown(msgParam.getString("markdown"));
                msg.getActionCard().setBtnOrientation(msgParam.getString("btn_orientation"));
                msg.getActionCard().setSingleTitle(msgParam.getString("single_title"));
                msg.getActionCard().setSingleUrl(msgParam.getString("single_url"));
                if (!msgParam.containsKey("btn_json_list")) break;
                ArrayList<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList> btnList = new ArrayList<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList>();
                JSONArray btnArr = msgParam.getJSONArray("btn_json_list");
                for (int index = 0; index < btnArr.size(); ++index) {
                    JSONObject btnObj = btnArr.getJSONObject(index);
                    OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList btnJsonItem = new OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList();
                    btnJsonItem.setTitle(btnObj.getString("title"));
                    btnJsonItem.setActionUrl(btnObj.getString("action_url"));
                    btnList.add(btnJsonItem);
                }
                msg.getActionCard().setBtnJsonList(btnList);
                break;
            }
        }
        msg.setMsgtype(notificationMsgType.getType());
        req.setMsg(msg);
        BasePushResult result = new BasePushResult();
        try {
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, dingtalkService.getAccessToken().getResult());
            if (rsp.getErrorCode().equals("0")) {
                result.setState(1);
                result.setMoreMsg(String.valueOf(rsp.getTaskId()));
            } else {
                result.setState(-1);
                result.setErrorMsg(rsp.getErrmsg());
            }
        }
        catch (ApiException e) {
            result.setState(-1);
            result.setErrorMsg(e.getLocalizedMessage());
        }
        return result;
    }
}
