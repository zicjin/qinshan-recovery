/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.card.ICardMsgParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

public class InCardPayOrderEvent
extends EventInMsg
implements ICardMsgParse {
    public static final String EVENT = "card_pay_order";
    private String orderId;
    private String status;
    private String createOrderTime;
    private String payFinishTime;
    private String desc;
    private String freeCoinCount;
    private String payCoinCount;
    private String refundFreeCoinCount;
    private String refundPayCoinCount;
    private String orderType;
    private String memo;
    private String receiptInfo;

    public InCardPayOrderEvent(String toUserName, String fromUserName, Integer createTime) {
        super(toUserName, fromUserName, createTime, EVENT);
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateOrderTime() {
        return this.createOrderTime;
    }

    public void setCreateOrderTime(String createOrderTime) {
        this.createOrderTime = createOrderTime;
    }

    public String getPayFinishTime() {
        return this.payFinishTime;
    }

    public void setPayFinishTime(String payFinishTime) {
        this.payFinishTime = payFinishTime;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFreeCoinCount() {
        return this.freeCoinCount;
    }

    public void setFreeCoinCount(String freeCoinCount) {
        this.freeCoinCount = freeCoinCount;
    }

    public String getPayCoinCount() {
        return this.payCoinCount;
    }

    public void setPayCoinCount(String payCoinCount) {
        this.payCoinCount = payCoinCount;
    }

    public String getRefundFreeCoinCount() {
        return this.refundFreeCoinCount;
    }

    public void setRefundFreeCoinCount(String refundFreeCoinCount) {
        this.refundFreeCoinCount = refundFreeCoinCount;
    }

    public String getRefundPayCoinCount() {
        return this.refundPayCoinCount;
    }

    public void setRefundPayCoinCount(String refundPayCoinCount) {
        this.refundPayCoinCount = refundPayCoinCount;
    }

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getReceiptInfo() {
        return this.receiptInfo;
    }

    public void setReceiptInfo(String receiptInfo) {
        this.receiptInfo = receiptInfo;
    }

    @Override
    public void parse(XmlHelper xmlHelper) {
        this.setOrderId(xmlHelper.getString("//OrderId"));
        this.setStatus(xmlHelper.getString("//Status"));
        this.setCreateOrderTime(xmlHelper.getString("//CreateOrderTime"));
        this.setPayFinishTime(xmlHelper.getString("//PayFinishTime"));
        this.setDesc(xmlHelper.getString("//Desc"));
        this.setFreeCoinCount(xmlHelper.getString("//FreeCoinCount"));
        this.setPayCoinCount(xmlHelper.getString("//PayCoinCount"));
        this.setRefundFreeCoinCount(xmlHelper.getString("//RefundFreeCoinCount"));
        this.setRefundPayCoinCount(xmlHelper.getString("//RefundPayCoinCount"));
        this.setOrderType(xmlHelper.getString("//OrderType"));
        this.setOrderType(xmlHelper.getString("//OrderType"));
        this.setMemo(xmlHelper.getString("//Memo"));
        this.setReceiptInfo(xmlHelper.getString("//ReceiptInfo"));
    }
}
