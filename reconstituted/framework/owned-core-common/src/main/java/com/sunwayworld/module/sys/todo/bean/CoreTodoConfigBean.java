/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_TODO_CONFIG")
public class CoreTodoConfigBean
extends AbstractPersistable<Long>
implements Activatable<Long> {
    private static final long serialVersionUID = 5889667481997735018L;
    @Id
    private Long id;
    private String name;
    private String menuId;
    private String category;
    private String descExpression;
    private String icon;
    @NotNull(defaultValue="1")
    private String inheritParam;
    @NotNull(defaultValue="1")
    private String voiceWarning;
    private String jsonParam;
    private String serviceMethod;
    private String lcdpServiceMethod;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    private Long orderNo;
    private String i18nCode;
    @Clob
    private String selectSql;
    @Clob
    private String mobileSelectSql;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescExpression() {
        return this.descExpression;
    }

    public void setDescExpression(String descExpression) {
        this.descExpression = descExpression;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInheritParam() {
        return this.inheritParam;
    }

    public void setInheritParam(String inheritParam) {
        this.inheritParam = inheritParam;
    }

    public String getJsonParam() {
        return this.jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public String getVoiceWarning() {
        return this.voiceWarning;
    }

    public void setVoiceWarning(String voiceWarning) {
        this.voiceWarning = voiceWarning;
    }

    public String getServiceMethod() {
        return this.serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public String getLcdpServiceMethod() {
        return this.lcdpServiceMethod;
    }

    public void setLcdpServiceMethod(String lcdpServiceMethod) {
        this.lcdpServiceMethod = lcdpServiceMethod;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getI18nCode() {
        return this.i18nCode;
    }

    public void setI18nCode(String i18nCode) {
        this.i18nCode = i18nCode;
    }

    public String getSelectSql() {
        return this.selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public String getMobileSelectSql() {
        return this.mobileSelectSql;
    }

    public void setMobileSelectSql(String mobileSelectSql) {
        this.mobileSelectSql = mobileSelectSql;
    }
}
