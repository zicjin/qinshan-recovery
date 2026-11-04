/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InUserCardEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT_MEMBERCARD = "submit_membercard_user_info";
    public static final String EVENT_USER_VIEW = "user_view_card";
    public static final String EVENT_USER_ENTER = "user_enter_session_from_card";
    public static final String EVENT_USER_DEL = "user_del_card";
    private String cardId;
    private String userCardCode;

    public InUserCardEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
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

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setCardId(xmlHelper.getString("//CardId"));
        this.setUserCardCode(xmlHelper.getString("//UserCardCode"));
    }
}
