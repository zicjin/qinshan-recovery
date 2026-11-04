/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MOBILE_WECHAT_CPUSER")
public class CpUserInfo
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 8174891822247105314L;
    @Id
    private Long id;
    private String cpuserid;
    private Integer agentid;
    private String userid;
    private String usercode;
    private String locale;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCpuserid() {
        return this.cpuserid;
    }

    public Integer getAgentid() {
        return this.agentid;
    }

    public String getUserid() {
        return this.userid;
    }

    public String getUsercode() {
        return this.usercode;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setCpuserid(String cpuserid) {
        this.cpuserid = cpuserid;
    }

    public void setAgentid(Integer agentid) {
        this.agentid = agentid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
