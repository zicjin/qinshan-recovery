/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MOBILE_PUSHUSER")
public class PushUserInfo
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = -2733760841905426489L;
    @Id
    private Long id;
    private String pushuserid;
    private String usercode;
    private String devicetoken;
    private String devicetype;
    private String locale;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPushuserid() {
        return this.pushuserid;
    }

    public void setPushuserid(String pushuserid) {
        this.pushuserid = pushuserid;
    }

    public String getUsercode() {
        return this.usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getDevicetoken() {
        return this.devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getDevicetype() {
        return this.devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
