/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InUserGetCardEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT = "user_get_card";
    private String cardId;
    private String isGiveByFriend;
    private String userCardCode;
    private String friendUserName;
    private String outerId;
    private String oldUserCardCode;
    private String outerStr;
    private String isRestoreMemberCard;
    private String isRecommendByFriend;

    public InUserGetCardEvent(String toUserName, String fromUserName, Integer createTime) {
        super(toUserName, fromUserName, createTime, EVENT);
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIsGiveByFriend() {
        return this.isGiveByFriend;
    }

    public void setIsGiveByFriend(String isGiveByFriend) {
        this.isGiveByFriend = isGiveByFriend;
    }

    public String getUserCardCode() {
        return this.userCardCode;
    }

    public void setUserCardCode(String userCardCode) {
        this.userCardCode = userCardCode;
    }

    public String getOuterId() {
        return this.outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getFriendUserName() {
        return this.friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public String getOldUserCardCode() {
        return this.oldUserCardCode;
    }

    public void setOldUserCardCode(String oldUserCardCode) {
        this.oldUserCardCode = oldUserCardCode;
    }

    public String getOuterStr() {
        return this.outerStr;
    }

    public void setOuterStr(String outerStr) {
        this.outerStr = outerStr;
    }

    public String getIsRestoreMemberCard() {
        return this.isRestoreMemberCard;
    }

    public void setIsRestoreMemberCard(String isRestoreMemberCard) {
        this.isRestoreMemberCard = isRestoreMemberCard;
    }

    public String getIsRecommendByFriend() {
        return this.isRecommendByFriend;
    }

    public void setIsRecommendByFriend(String isRecommendByFriend) {
        this.isRecommendByFriend = isRecommendByFriend;
    }

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setCardId(xmlHelper.getString("//CardId"));
        this.setIsGiveByFriend(xmlHelper.getString("//IsGiveByFriend"));
        this.setUserCardCode(xmlHelper.getString("//UserCardCode"));
        this.setFriendUserName(xmlHelper.getString("//FriendUserName"));
        this.setOuterId(xmlHelper.getString("//OuterId"));
        this.setOldUserCardCode(xmlHelper.getString("//OldUserCardCode"));
        this.setOuterStr(xmlHelper.getString("//OuterStr"));
        this.setIsRestoreMemberCard(xmlHelper.getString("//IsRestoreMemberCard"));
        this.setIsRecommendByFriend(xmlHelper.getString("//IsRecommendByFriend"));
    }
}
