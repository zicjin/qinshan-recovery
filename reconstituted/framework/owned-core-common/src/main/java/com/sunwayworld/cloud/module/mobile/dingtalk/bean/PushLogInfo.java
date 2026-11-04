/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MOBILE_PUSHLOG")
public class PushLogInfo
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = -4775638904565889470L;
    @Id
    private Long id;
    private String pushlogid;
    private String pushdata;
    private String pushdate;
    private String pushuser;
    private String pushdataid;
    private int readstatus;
    private int auditstatus;
    private String devicetype;
    private String pushresult;
    private String coid;
    private String locale;
    private String title;
    private String content;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPushlogid() {
        return this.pushlogid;
    }

    public void setPushlogid(String pushlogid) {
        this.pushlogid = pushlogid;
    }

    public String getPushdata() {
        return this.pushdata;
    }

    public void setPushdata(String pushdata) {
        this.pushdata = pushdata;
    }

    public String getPushdate() {
        return this.pushdate;
    }

    public void setPushdate(String pushdate) {
        this.pushdate = pushdate;
    }

    public String getPushuser() {
        return this.pushuser;
    }

    public void setPushuser(String pushuser) {
        this.pushuser = pushuser;
    }

    public String getPushdataid() {
        return this.pushdataid;
    }

    public void setPushdataid(String pushdataid) {
        this.pushdataid = pushdataid;
    }

    public int getReadstatus() {
        return this.readstatus;
    }

    public void setReadstatus(int readstatus) {
        this.readstatus = readstatus;
    }

    public int getAuditstatus() {
        return this.auditstatus;
    }

    public void setAuditstatus(int auditstatus) {
        this.auditstatus = auditstatus;
    }

    public String getDevicetype() {
        return this.devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getPushresult() {
        return this.pushresult;
    }

    public void setPushresult(String pushresult) {
        this.pushresult = pushresult;
    }

    public String getCoid() {
        return this.coid;
    }

    public void setCoid(String coid) {
        this.coid = coid;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
