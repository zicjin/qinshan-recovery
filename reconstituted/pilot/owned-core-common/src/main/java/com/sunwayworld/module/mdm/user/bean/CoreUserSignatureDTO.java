/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CoreUserSignatureDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -1849753352153268090L;
    private String signUserId;
    private String signUserName;
    @JSONField(format="yyyy-MM-dd")
    private LocalDate signDate;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signTime;
    private String signPicUrl;

    public String getSignUserId() {
        return this.signUserId;
    }

    public void setSignUserId(String signUserId) {
        this.signUserId = signUserId;
    }

    public String getSignUserName() {
        return this.signUserName;
    }

    public void setSignUserName(String signUserName) {
        this.signUserName = signUserName;
    }

    public LocalDate getSignDate() {
        return this.signDate;
    }

    public void setSignDate(LocalDate signDate) {
        this.signDate = signDate;
    }

    public LocalDateTime getSignTime() {
        return this.signTime;
    }

    public void setSignTime(LocalDateTime signTime) {
        this.signTime = signTime;
    }

    public String getSignPicUrl() {
        return this.signPicUrl;
    }

    public void setSignPicUrl(String signPicUrl) {
        this.signPicUrl = signPicUrl;
    }
}
