/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InUserConsumeCardEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT = "user_consume_card";
    private String cardId;
    private String userCardCode;
    private String consumeSource;
    private String locationName;
    private String staffOpenId;
    private String verifyCode;
    private String remarkAmount;
    private String outerStr;

    public InUserConsumeCardEvent(String toUserName, String fromUserName, Integer createTime) {
        super(toUserName, fromUserName, createTime, EVENT);
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getUserCardCode() {
        return this.userCardCode;
    }

    public void setUserCardCode(String userCardCode) {
        this.userCardCode = userCardCode;
    }

    public String getConsumeSource() {
        return this.consumeSource;
    }

    public void setConsumeSource(String consumeSource) {
        this.consumeSource = consumeSource;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStaffOpenId() {
        return this.staffOpenId;
    }

    public void setStaffOpenId(String staffOpenId) {
        this.staffOpenId = staffOpenId;
    }

    public String getVerifyCode() {
        return this.verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getRemarkAmount() {
        return this.remarkAmount;
    }

    public void setRemarkAmount(String remarkAmount) {
        this.remarkAmount = remarkAmount;
    }

    public String getOuterStr() {
        return this.outerStr;
    }

    public void setOuterStr(String outerStr) {
        this.outerStr = outerStr;
    }

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setCardId(xmlHelper.getString("//CardId"));
        this.setUserCardCode(xmlHelper.getString("//UserCardCode"));
        this.setConsumeSource(xmlHelper.getString("//ConsumeSource"));
        this.setLocationName(xmlHelper.getString("//LocationName"));
        this.setStaffOpenId(xmlHelper.getString("//StaffOpenId"));
        this.setVerifyCode(xmlHelper.getString("//VerifyCode"));
        this.setRemarkAmount(xmlHelper.getString("//RemarkAmount"));
        this.setOuterStr(xmlHelper.getString("//OuterStr"));
    }
}
