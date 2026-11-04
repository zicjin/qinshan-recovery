/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_EMAIL_RECEIPENT")
public class CoreEmailReceipentBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -5667305854400216889L;
    @Id
    private Long id;
    private Long emailId;
    private String receipentId;
    private String receipentName;
    private String receipentEmail;
    private String receipentCategory;
    @NotNull(defaultValue="0")
    private String success;
    @NotNull(defaultValue="0")
    private Integer reattempt;
    private String log;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmailId() {
        return this.emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getReceipentId() {
        return this.receipentId;
    }

    public void setReceipentId(String receipentId) {
        this.receipentId = receipentId;
    }

    public String getReceipentName() {
        return this.receipentName;
    }

    public void setReceipentName(String receipentName) {
        this.receipentName = receipentName;
    }

    public String getReceipentEmail() {
        return this.receipentEmail;
    }

    public void setReceipentEmail(String receipentEmail) {
        this.receipentEmail = receipentEmail;
    }

    public String getReceipentCategory() {
        return this.receipentCategory;
    }

    public void setReceipentCategory(String receipentCategory) {
        this.receipentCategory = receipentCategory;
    }

    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Integer getReattempt() {
        return this.reattempt;
    }

    public void setReattempt(Integer reattempt) {
        this.reattempt = reattempt;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
