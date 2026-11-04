/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import java.time.LocalDateTime;

public class CoreMenuDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 3501256693867350690L;
    private String id;
    private String menuName;
    private String menuInitName;
    private String icon;
    private String menuIcon;
    private String todoIcon;
    private String href;
    private String parentId;
    private String i18nCode;
    private Long orderNo;
    private Long resourceId;
    private String resourceName;
    private Long bpId;
    private String resourcePath;
    private String menuParam;
    private String clientScript;
    private LocalDateTime publishTime;
    private String publishUserId;
    private String publishUserName;
    private String publishStatus;

    public static CoreMenuDTO of(CoreMenuBean menu) {
        CoreMenuDTO instance = new CoreMenuDTO();
        instance.setId(menu.getId());
        instance.setMenuName(menu.getMenuName());
        instance.setMenuInitName(menu.getMenuInitName());
        instance.setIcon(menu.getIcon());
        instance.setTodoIcon(menu.getTodoIcon());
        instance.setHref(menu.getHref());
        instance.setParentId(menu.getParentId());
        instance.setI18nCode(menu.getI18nCode());
        instance.setOrderNo(menu.getOrderNo());
        instance.setResourceId(menu.getResourceId());
        instance.setResourceName(menu.getResourceName());
        instance.setBpId(menu.getBpId());
        instance.setResourcePath(menu.getResourcePath());
        instance.setMenuParam(menu.getMenuParam());
        instance.setClientScript(menu.getClientScript());
        instance.setPublishTime(menu.getPublishTime());
        instance.setPublishUserName(menu.getPublishUserName());
        return instance;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuInitName() {
        return this.menuInitName;
    }

    public void setMenuInitName(String menuInitName) {
        this.menuInitName = menuInitName;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMenuIcon() {
        return this.menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getTodoIcon() {
        return this.todoIcon;
    }

    public void setTodoIcon(String todoIcon) {
        this.todoIcon = todoIcon;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getI18nCode() {
        return this.i18nCode;
    }

    public void setI18nCode(String i18nCode) {
        this.i18nCode = i18nCode;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getMenuParam() {
        return this.menuParam;
    }

    public void setMenuParam(String menuParam) {
        this.menuParam = menuParam;
    }

    public String getClientScript() {
        return this.clientScript;
    }

    public void setClientScript(String clientScript) {
        this.clientScript = clientScript;
    }

    public LocalDateTime getPublishTime() {
        return this.publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishUserId() {
        return this.publishUserId;
    }

    public void setPublishUserId(String publishUserId) {
        this.publishUserId = publishUserId;
    }

    public String getPublishUserName() {
        return this.publishUserName;
    }

    public void setPublishUserName(String publishUserName) {
        this.publishUserName = publishUserName;
    }

    public String getPublishStatus() {
        return this.publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }
}
