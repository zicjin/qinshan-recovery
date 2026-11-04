/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MOBILE_PUSHLOGHISTORY")
public class PushLogInfoBackup
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

    public String getPushdata() {
        return this.pushdata;
    }

    public String getPushdate() {
        return this.pushdate;
    }

    public String getPushuser() {
        return this.pushuser;
    }

    public String getPushdataid() {
        return this.pushdataid;
    }

    public int getReadstatus() {
        return this.readstatus;
    }

    public int getAuditstatus() {
        return this.auditstatus;
    }

    public String getDevicetype() {
        return this.devicetype;
    }

    public String getPushresult() {
        return this.pushresult;
    }

    public String getCoid() {
        return this.coid;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setCoid(String coid) {
        this.coid = coid;
    }

    public void setPushlogid(String pushlogid) {
        this.pushlogid = pushlogid;
    }

    public void setPushdata(String pushdata) {
        this.pushdata = pushdata;
    }

    public void setPushdate(String pushdate) {
        this.pushdate = pushdate;
    }

    public void setPushuser(String pushuser) {
        this.pushuser = pushuser;
    }

    public void setPushdataid(String pushdataid) {
        this.pushdataid = pushdataid;
    }

    public void setReadstatus(int readstatus) {
        this.readstatus = readstatus;
    }

    public void setAuditstatus(int auditstatus) {
        this.auditstatus = auditstatus;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public void setPushresult(String pushresult) {
        this.pushresult = pushresult;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
