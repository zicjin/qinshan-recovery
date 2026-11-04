/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MOBILE_DINGTALK_USER")
public class DingtalkUserBean
extends AbstractInsertable<String>
implements Insertable<String> {
    private static final long serialVersionUID = 8174891822247105314L;
    @Id
    private String id;
    private String dtuserid;
    private String agentid;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDtuserid() {
        return this.dtuserid;
    }

    public String getAgentid() {
        return this.agentid;
    }

    public void setDtuserid(String dtuserid) {
        this.dtuserid = dtuserid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }
}
