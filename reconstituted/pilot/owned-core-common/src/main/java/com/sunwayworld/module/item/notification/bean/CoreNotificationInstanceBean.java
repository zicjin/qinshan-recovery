/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_NOTIFICATION_INST")
public class CoreNotificationInstanceBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = 5413560956672847239L;
    @Id
    private Long id;
    private String templateId;
    private String title;
    private String viewMethod;
    private String menuId;
    private String menuName;
    private String pageUrl;
    @Clob
    private String content;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @Clob
    private String param;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewMethod() {
        return this.viewMethod;
    }

    public void setViewMethod(String viewMethod) {
        this.viewMethod = viewMethod;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
