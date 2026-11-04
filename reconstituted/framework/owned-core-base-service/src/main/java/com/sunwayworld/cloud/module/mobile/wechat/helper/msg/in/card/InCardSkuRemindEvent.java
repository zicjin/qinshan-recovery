/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InCardSkuRemindEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT = "card_pay_order";
    private String cardId;
    private String detail;

    public InCardSkuRemindEvent(String toUserName, String fromUserName, Integer createTime) {
        super(toUserName, fromUserName, createTime, EVENT);
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setCardId(xmlHelper.getString("//CardId"));
        this.setDetail(xmlHelper.getString("//Detail"));
    }
}
