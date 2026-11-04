/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class CoreNotificationMessageDTO
implements Serializable {
    private static final long serialVersionUID = -8737542376094140668L;
    private Long id;
    private String title;
    private String viewMethod;
    private String menuId;
    private String menuName;
    private String pageUrl;
    private String content;
    private String param;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String read;
    private List<CoreNotificationInstanceFileBean> fileList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public List<CoreNotificationInstanceFileBean> getFileList() {
        return this.fileList;
    }

    public void setFileList(List<CoreNotificationInstanceFileBean> fileList) {
        this.fileList = fileList;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
