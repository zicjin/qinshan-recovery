/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import java.util.ArrayList;
import java.util.List;

public class LcdpI18nDTO
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = -1625507271599294334L;
    private Long id;
    private Long modulePageHistoryId;
    private String code;
    private String description;
    private String defaultMessage;
    private String path;
    private List<LcdpModulePageI18nBean> messageList = new ArrayList<LcdpModulePageI18nBean>();

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getModulePageHistoryId() {
        return this.modulePageHistoryId;
    }

    public void setModulePageHistoryId(Long modulePageHistoryId) {
        this.modulePageHistoryId = modulePageHistoryId;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<LcdpModulePageI18nBean> getMessageList() {
        return this.messageList;
    }

    public void setMessageList(List<LcdpModulePageI18nBean> messageList) {
        this.messageList = messageList;
    }
}
