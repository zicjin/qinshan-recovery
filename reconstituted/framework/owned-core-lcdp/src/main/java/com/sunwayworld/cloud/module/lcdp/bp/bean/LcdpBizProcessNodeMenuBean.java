/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_BIZ_PROCESS_NODE_MENU")
public class LcdpBizProcessNodeMenuBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = 6432067205927057620L;
    @Id
    private Long id;
    private Long bpId;
    private String parentMenuId;
    private String parentMenuName;
    private String menuId;
    private String menuName;
    private String englishMenuName;
    private String chineseMenuName;
    @NotNull(defaultValue="0")
    private String regenerateFlag;
    private Long orderNo;
    private Long resourceId;
    private String resourceName;
    private String resourcePath;
    @Clob
    private String clientScript;
    @Clob
    private String todoScript;
    @Clob
    private String menuParam;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String icon;
    private String todoIcon;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getParentMenuId() {
        return this.parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public String getParentMenuName() {
        return this.parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
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

    public String getEnglishMenuName() {
        return this.englishMenuName;
    }

    public void setEnglishMenuName(String englishMenuName) {
        this.englishMenuName = englishMenuName;
    }

    public String getChineseMenuName() {
        return this.chineseMenuName;
    }

    public void setChineseMenuName(String chineseMenuName) {
        this.chineseMenuName = chineseMenuName;
    }

    public String getRegenerateFlag() {
        return this.regenerateFlag;
    }

    public void setRegenerateFlag(String regenerateFlag) {
        this.regenerateFlag = regenerateFlag;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
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

    public String getClientScript() {
        return this.clientScript;
    }

    public void setClientScript(String clientScript) {
        this.clientScript = clientScript;
    }

    public String getTodoScript() {
        return this.todoScript;
    }

    public void setTodoScript(String todoScript) {
        this.todoScript = todoScript;
    }

    public String getMenuParam() {
        return this.menuParam;
    }

    public void setMenuParam(String menuParam) {
        this.menuParam = menuParam;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTodoIcon() {
        return this.todoIcon;
    }

    public void setTodoIcon(String todoIcon) {
        this.todoIcon = todoIcon;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
