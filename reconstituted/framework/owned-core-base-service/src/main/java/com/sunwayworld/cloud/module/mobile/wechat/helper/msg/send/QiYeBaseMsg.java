/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send;

public class QiYeBaseMsg {
    private String touser;
    private String toparty;
    protected String msgtype;
    private String agentid;
    private String safe;

    public String getTouser() {
        return this.touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return this.toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getAgentid() {
        return this.agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public QiYeBaseMsg(String touser, String toparty, String agentid) {
        this.touser = touser;
        this.toparty = toparty;
        this.agentid = agentid;
    }

    public QiYeBaseMsg() {
    }

    public String getSafe() {
        return this.safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }
}
