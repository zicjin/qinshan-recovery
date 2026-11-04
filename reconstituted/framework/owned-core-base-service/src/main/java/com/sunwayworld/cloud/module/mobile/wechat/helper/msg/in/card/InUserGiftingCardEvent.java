/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InUserGiftingCardEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT = "user_gifting_card";
    private String cardId;
    private String userCardCode;
    private String isReturnBack;
    private String friendUserName;
    private String isChatRoom;

    public InUserGiftingCardEvent(String toUserName, String fromUserName, Integer createTime) {
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

    public String getIsReturnBack() {
        return this.isReturnBack;
    }

    public void setIsReturnBack(String isReturnBack) {
        this.isReturnBack = isReturnBack;
    }

    public String getFriendUserName() {
        return this.friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public String getIsChatRoom() {
        return this.isChatRoom;
    }

    public void setIsChatRoom(String isChatRoom) {
        this.isChatRoom = isChatRoom;
    }

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setCardId(xmlHelper.getString("//CardId"));
        this.setUserCardCode(xmlHelper.getString("//UserCardCode"));
        this.setIsReturnBack(xmlHelper.getString("//IsReturnBack"));
        this.setFriendUserName(xmlHelper.getString("//FriendUserName"));
        this.setIsChatRoom(xmlHelper.getString("//IsChatRoom"));
    }
}
