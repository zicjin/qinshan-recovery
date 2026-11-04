/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InImageMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InLinkMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InLocationMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InNotDefinedMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InShortVideoMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InTextMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InVideoMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InVoiceMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InCardPassCheckEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InCardPayOrderEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InCardSkuRemindEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InMerChantOrderEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUpdateMemberCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUserCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUserConsumeCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUserGetCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUserGiftingCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.InUserPayFromCardEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InCustomEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InFollowEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InLocationEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InMassEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InMenuEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InNotDefinedEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InPoiCheckNotifyEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InQrCodeEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InShakearoundUserShakeEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InTemplateMsgEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InVerifyFailEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InVerifySuccessEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.InWifiEvent;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.ScanCodeInfo;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.msg.InEquDataMsg;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.msg.InEqubindEvent;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InMsgParser {
    private InMsgParser() {
    }

    public static InMsg parse(String xml) {
        XmlHelper xmlHelper = XmlHelper.of(xml);
        return InMsgParser.doParse(xmlHelper);
    }

    private static InMsg doParse(XmlHelper xmlHelper) {
        String toUserName = xmlHelper.getString("//ToUserName");
        String fromUserName = xmlHelper.getString("//FromUserName");
        Integer createTime = xmlHelper.getNumber("//CreateTime").intValue();
        String msgType = xmlHelper.getString("//MsgType");
        if ("text".equals(msgType)) {
            return InMsgParser.parseInTextMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("image".equals(msgType)) {
            return InMsgParser.parseInImageMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("voice".equals(msgType)) {
            return InMsgParser.parseInVoiceMsgAndInSpeechRecognitionResults(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("video".equals(msgType)) {
            return InMsgParser.parseInVideoMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("shortvideo".equals(msgType)) {
            return InMsgParser.parseInShortVideoMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("location".equals(msgType)) {
            return InMsgParser.parseInLocationMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("link".equals(msgType)) {
            return InMsgParser.parseInLinkMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("event".equals(msgType)) {
            return InMsgParser.parseInEvent(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("device_event".equals(msgType)) {
            return InMsgParser.parseInDeviceEvent(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        if ("device_text".equals(msgType)) {
            return InMsgParser.parseInDeviceData(xmlHelper, toUserName, fromUserName, createTime, msgType);
        }
        return InMsgParser.parseInNotDefinedMsg(xmlHelper, toUserName, fromUserName, createTime, msgType);
    }

    private static InMsg parseInNotDefinedMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InNotDefinedMsg msg = new InNotDefinedMsg(toUserName, fromUserName, createTime, msgType);
        msg.setXmlHelper(xmlHelper);
        return msg;
    }

    private static InMsg parseInTextMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InTextMsg msg = new InTextMsg(toUserName, fromUserName, createTime, msgType);
        msg.setContent(xmlHelper.getString("//Content"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInImageMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InImageMsg msg = new InImageMsg(toUserName, fromUserName, createTime, msgType);
        msg.setPicUrl(xmlHelper.getString("//PicUrl"));
        msg.setMediaId(xmlHelper.getString("//MediaId"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInVoiceMsgAndInSpeechRecognitionResults(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        String recognition = xmlHelper.getString("//Recognition");
        String mediaId = xmlHelper.getString("//MediaId");
        String format = xmlHelper.getString("//Format");
        String msgId = xmlHelper.getString("//MsgId");
        if (StringUtils.isBlank(recognition)) {
            InVoiceMsg msg = new InVoiceMsg(toUserName, fromUserName, createTime, msgType);
            msg.setMediaId(mediaId);
            msg.setFormat(format);
            msg.setMsgId(msgId);
            return msg;
        }
        InSpeechRecognitionResults msg = new InSpeechRecognitionResults(toUserName, fromUserName, createTime, msgType);
        msg.setMediaId(mediaId);
        msg.setFormat(format);
        msg.setMsgId(msgId);
        msg.setRecognition(recognition);
        return msg;
    }

    private static InMsg parseInVideoMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InVideoMsg msg = new InVideoMsg(toUserName, fromUserName, createTime, msgType);
        msg.setMediaId(xmlHelper.getString("//MediaId"));
        msg.setThumbMediaId(xmlHelper.getString("//ThumbMediaId"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInShortVideoMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InShortVideoMsg msg = new InShortVideoMsg(toUserName, fromUserName, createTime, msgType);
        msg.setMediaId(xmlHelper.getString("//MediaId"));
        msg.setThumbMediaId(xmlHelper.getString("//ThumbMediaId"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInLocationMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InLocationMsg msg = new InLocationMsg(toUserName, fromUserName, createTime, msgType);
        msg.setLocation_X(xmlHelper.getString("//Location_X"));
        msg.setLocation_Y(xmlHelper.getString("//Location_Y"));
        msg.setScale(xmlHelper.getString("//Scale"));
        msg.setLabel(xmlHelper.getString("//Label"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInLinkMsg(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InLinkMsg msg = new InLinkMsg(toUserName, fromUserName, createTime, msgType);
        msg.setTitle(xmlHelper.getString("//Title"));
        msg.setDescription(xmlHelper.getString("//Description"));
        msg.setUrl(xmlHelper.getString("//Url"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInDeviceEvent(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        String event = xmlHelper.getString("//Event");
        InEqubindEvent e = new InEqubindEvent(toUserName, fromUserName, createTime, msgType, event);
        e.setDeviceID(xmlHelper.getString("//DeviceID"));
        e.setDeviceType(xmlHelper.getString("//DeviceType"));
        e.setOpenID(xmlHelper.getString("//OpenID"));
        e.setSessionID(xmlHelper.getString("//SessionID"));
        return e;
    }

    private static InMsg parseInDeviceData(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        InEquDataMsg msg = new InEquDataMsg(toUserName, fromUserName, createTime, msgType);
        msg.setContent(xmlHelper.getString("//Content"));
        msg.setDeviceID(xmlHelper.getString("//DeviceID"));
        msg.setDeviceType(xmlHelper.getString("//DeviceType"));
        msg.setOpenID(xmlHelper.getString("//OpenID"));
        msg.setMsgId(xmlHelper.getString("//MsgId"));
        return msg;
    }

    private static InMsg parseInEvent(XmlHelper xmlHelper, String toUserName, String fromUserName, Integer createTime, String msgType) {
        String event = xmlHelper.getString("//Event");
        String eventKey = xmlHelper.getString("//EventKey");
        if ("unsubscribe".equals(event)) {
            return new InFollowEvent(toUserName, fromUserName, createTime, event, eventKey);
        }
        String ticket = xmlHelper.getString("//Ticket");
        if ("subscribe".equals(event) && !StringUtils.isBlank(eventKey) && eventKey.startsWith("qrscene_")) {
            InQrCodeEvent e = new InQrCodeEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            e.setTicket(ticket);
            return e;
        }
        if ("SCAN".equals(event)) {
            InQrCodeEvent e = new InQrCodeEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            e.setTicket(ticket);
            return e;
        }
        if ("subscribe".equals(event)) {
            return new InFollowEvent(toUserName, fromUserName, createTime, event, eventKey);
        }
        if ("LOCATION".equals(event)) {
            InLocationEvent e = new InLocationEvent(toUserName, fromUserName, createTime, event);
            e.setLatitude(xmlHelper.getString("//Latitude"));
            e.setLongitude(xmlHelper.getString("//Longitude"));
            e.setPrecision(xmlHelper.getString("//Precision"));
            return e;
        }
        if ("CLICK".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("VIEW".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("scancode_push".equals(event) || "scancode_waitmsg".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            String scanType = xmlHelper.getString("//ScanCodeInfo/ScanType");
            String scanResult = xmlHelper.getString("//ScanCodeInfo/ScanResult");
            e.setScanCodeInfo(new ScanCodeInfo(scanType, scanResult));
            return e;
        }
        if ("pic_sysphoto".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("pic_photo_or_album".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("pic_weixin".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("location_select".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("media_id".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("view_limited".equals(event)) {
            InMenuEvent e = new InMenuEvent(toUserName, fromUserName, createTime, event);
            e.setEventKey(eventKey);
            return e;
        }
        if ("TEMPLATESENDJOBFINISH".equals(event)) {
            InTemplateMsgEvent e = new InTemplateMsgEvent(toUserName, fromUserName, createTime, event);
            e.setMsgId(xmlHelper.getString("//MsgID"));
            e.setStatus(xmlHelper.getString("//Status"));
            return e;
        }
        if ("MASSSENDJOBFINISH".equals(event)) {
            InMassEvent e = new InMassEvent(toUserName, fromUserName, createTime, event);
            e.setMsgId(xmlHelper.getString("//MsgID"));
            e.setStatus(xmlHelper.getString("//Status"));
            e.setTotalCount(xmlHelper.getString("//TotalCount"));
            e.setFilterCount(xmlHelper.getString("//FilterCount"));
            e.setSentCount(xmlHelper.getString("//SentCount"));
            e.setErrorCount(xmlHelper.getString("//ErrorCount"));
            return e;
        }
        if ("kf_create_session".equals(event)) {
            InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, event);
            e.setKfAccount(xmlHelper.getString("//KfAccount"));
            return e;
        }
        if ("kf_close_session".equals(event)) {
            InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, event);
            e.setKfAccount(xmlHelper.getString("//KfAccount"));
            return e;
        }
        if ("kf_switch_session".equals(event)) {
            InCustomEvent e = new InCustomEvent(toUserName, fromUserName, createTime, event);
            e.setKfAccount(xmlHelper.getString("//KfAccount"));
            e.setToKfAccount(xmlHelper.getString("//ToKfAccount"));
            return e;
        }
        if ("ShakearoundUserShake".equals(event)) {
            InShakearoundUserShakeEvent e = new InShakearoundUserShakeEvent(toUserName, fromUserName, createTime, msgType);
            e.setEvent(event);
            e.setUuid(xmlHelper.getString("//ChosenBeacon/Uuid"));
            e.setMajor(xmlHelper.getNumber("//ChosenBeacon/Major").intValue());
            e.setMinor(xmlHelper.getNumber("//ChosenBeacon/Minor").intValue());
            e.setDistance(Float.valueOf(xmlHelper.getNumber("//ChosenBeacon/Distance").floatValue()));
            NodeList nodeList = xmlHelper.getNodeList("//AroundBeacons/AroundBeacon");
            if (nodeList != null && nodeList.getLength() > 0) {
                InShakearoundUserShakeEvent.AroundBeacon aroundBeacon = null;
                ArrayList<InShakearoundUserShakeEvent.AroundBeacon> aroundBeacons = new ArrayList<InShakearoundUserShakeEvent.AroundBeacon>();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node node = nodeList.item(i);
                    aroundBeacon = new InShakearoundUserShakeEvent.AroundBeacon();
                    aroundBeacon.setUuid(xmlHelper.getString(node, "Uuid"));
                    aroundBeacon.setMajor(xmlHelper.getNumber(node, "Major").intValue());
                    aroundBeacon.setMinor(xmlHelper.getNumber(node, "Minor").intValue());
                    aroundBeacon.setDistance(Float.valueOf(xmlHelper.getNumber(node, "Distance").floatValue()));
                    aroundBeacons.add(aroundBeacon);
                }
                e.setAroundBeaconList(aroundBeacons);
            }
            return e;
        }
        if ("qualification_verify_success".equals(event) || "naming_verify_success".equals(event) || "annual_renew".equals(event) || "verify_expired".equals(event)) {
            InVerifySuccessEvent e = new InVerifySuccessEvent(toUserName, fromUserName, createTime, event);
            e.setExpiredTime(xmlHelper.getString("//expiredTime"));
            return e;
        }
        if ("qualification_verify_fail".equals(event) || "naming_verify_fail".equals(event)) {
            InVerifyFailEvent e = new InVerifyFailEvent(toUserName, fromUserName, createTime, event);
            e.setFailTime(xmlHelper.getString("//failTime"));
            e.setFailReason(xmlHelper.getString("//failReason"));
            return e;
        }
        if ("poi_check_notify".equals(event)) {
            InPoiCheckNotifyEvent e = new InPoiCheckNotifyEvent(toUserName, fromUserName, createTime, event);
            e.setUniqId(xmlHelper.getString("//UniqId"));
            e.setPoiId(xmlHelper.getString("//PoiId"));
            e.setResult(xmlHelper.getString("//Result"));
            e.setMsg(xmlHelper.getString("//Msg"));
            return e;
        }
        if ("WifiConnected".equals(event)) {
            InWifiEvent e = new InWifiEvent(toUserName, fromUserName, createTime);
            e.setConnectTime(xmlHelper.getString("//ConnectTime"));
            e.setExpireTime(xmlHelper.getString("//ExpireTime"));
            e.setVendorId(xmlHelper.getString("//VendorId"));
            e.setDeviceNo(xmlHelper.getString("//DeviceNo"));
            e.setShopId(xmlHelper.getString("//ShopId"));
            return e;
        }
        if ("user_view_card".equals(event)) {
            InUserCardEvent e = new InUserCardEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("submit_membercard_user_info".equals(event)) {
            InUserCardEvent e = new InUserCardEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("update_member_card".equals(event)) {
            InUpdateMemberCardEvent e = new InUpdateMemberCardEvent(toUserName, fromUserName, createTime);
            e.setCardId(xmlHelper.getString("//CardId"));
            e.setUserCardCode(xmlHelper.getString("//UserCardCode"));
            e.setModifyBonus(xmlHelper.getString("//ModifyBonus"));
            e.setModifyBalance(xmlHelper.getString("//ModifyBalance"));
            return e;
        }
        if ("user_pay_from_pay_cell".equals(event)) {
            InUserPayFromCardEvent e = new InUserPayFromCardEvent(toUserName, fromUserName, createTime);
            e.setCardId(xmlHelper.getString("//CardId"));
            e.setUserCardCode(xmlHelper.getString("//UserCardCode"));
            e.setLocationId(xmlHelper.getString("//LocationId"));
            e.setTransId(xmlHelper.getString("//TransId"));
            e.setFee(xmlHelper.getString("//Fee"));
            e.setOriginalFee(xmlHelper.getString("//OriginalFee"));
            return e;
        }
        if ("merchant_order".equals(event)) {
            InMerChantOrderEvent e = new InMerChantOrderEvent(toUserName, fromUserName, createTime);
            e.setOrderId(xmlHelper.getString("//OrderId"));
            e.setOrderStatus(xmlHelper.getNumber("//OrderStatus").intValue());
            e.setProductId(xmlHelper.getString("//ProductId"));
            e.setSkuInfo(xmlHelper.getString("//SkuInfo"));
            return e;
        }
        if ("card_pass_check".equals(event)) {
            InCardPassCheckEvent e = new InCardPassCheckEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("card_not_pass_check".equals(event)) {
            InCardPassCheckEvent e = new InCardPassCheckEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("card_pay_order".equals(event)) {
            InCardPayOrderEvent e = new InCardPayOrderEvent(toUserName, fromUserName, createTime);
            e.parse(xmlHelper);
            return e;
        }
        if ("card_pay_order".equals(event)) {
            InCardSkuRemindEvent e = new InCardSkuRemindEvent(toUserName, fromUserName, createTime);
            e.parse(xmlHelper);
            return e;
        }
        if ("user_consume_card".equals(event)) {
            InUserConsumeCardEvent e = new InUserConsumeCardEvent(toUserName, fromUserName, createTime);
            e.parse(xmlHelper);
            return e;
        }
        if ("user_del_card".equals(event)) {
            InUserCardEvent e = new InUserCardEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("user_enter_session_from_card".equals(event)) {
            InUserCardEvent e = new InUserCardEvent(toUserName, fromUserName, createTime, event);
            e.parse(xmlHelper);
            return e;
        }
        if ("user_get_card".equals(event)) {
            InUserGetCardEvent e = new InUserGetCardEvent(toUserName, fromUserName, createTime);
            e.parse(xmlHelper);
            return e;
        }
        if ("user_gifting_card".equals(event)) {
            InUserGiftingCardEvent e = new InUserGiftingCardEvent(toUserName, fromUserName, createTime);
            e.parse(xmlHelper);
            return e;
        }
        InNotDefinedEvent e = new InNotDefinedEvent(toUserName, fromUserName, createTime, event);
        e.setXmlHelper(xmlHelper);
        return e;
    }
}
