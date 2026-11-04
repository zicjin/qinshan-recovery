/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreImContactsDTO
implements Serializable {
    private static final long serialVersionUID = -971878280142761646L;
    private String id;
    private String userId;
    private String userName;
    private String avatarUrl;
    private String signature;
    private String status;
    private String email;
    private String orgName;
    private Long offlineMessageQty;
    private CoreImMessageBean lastestMessage;
    private String top;
    private String deleted;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastestMessageTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOfflineMessageQty() {
        return this.offlineMessageQty;
    }

    public void setOfflineMessageQty(Long offlineMessageQty) {
        this.offlineMessageQty = offlineMessageQty;
    }

    public CoreImMessageBean getLastestMessage() {
        return this.lastestMessage;
    }

    public void setLastestMessage(CoreImMessageBean lastestMessage) {
        this.lastestMessage = lastestMessage;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTop() {
        return this.top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getDeleted() {
        return this.deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getLastestMessageTime() {
        return this.lastestMessageTime;
    }

    public void setLastestMessageTime(LocalDateTime lastestMessageTime) {
        this.lastestMessageTime = lastestMessageTime;
    }
}
