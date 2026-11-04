/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;

public class InUpdateMemberCardEvent
extends EventInMsg {
    public static final String EVENT = "update_member_card";
    private String cardId;
    private String userCardCode;
    private String modifyBonus;
    private String modifyBalance;

    public InUpdateMemberCardEvent(String toUserName, String fromUserName, Integer createTime) {
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

    public String getModifyBalance() {
        return this.modifyBalance;
    }

    public void setModifyBalance(String modifyBalance) {
        this.modifyBalance = modifyBalance;
    }

    public String getModifyBonus() {
        return this.modifyBonus;
    }

    public void setModifyBonus(String modifyBonus) {
        this.modifyBonus = modifyBonus;
    }
}
