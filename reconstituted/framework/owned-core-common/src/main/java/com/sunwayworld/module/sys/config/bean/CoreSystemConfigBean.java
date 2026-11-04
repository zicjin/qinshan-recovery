/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_SYSTEM_CONFIG")
public class CoreSystemConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    private static final long serialVersionUID = 3740353677226435899L;
    @Id
    private String id;
    private String pageStyle;
    private String pageSkin;
    private String specialCharacter;
    private String todoVoicePrompt;
    private String helpManualOperation;
    private String rollingNotice;
    private String notification;
    private String notificationAudio;
    private String version;
    private String sunwayOfficePreload;
    private String elnPreload;
    private String fileOperationLog;
    private String ieAccessTip;
    private String fullTextSearchResult;
    private Long speedTestPeriod;
    private String initPasswordPolicy;
    private String defaultPassword;
    private String helpCenter;
    private String systemVersion;
    private String todoDisplayCategory;
    private String licenseInfo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getPageStyle() {
        return this.pageStyle;
    }

    public void setPageStyle(String pageStyle) {
        this.pageStyle = pageStyle;
    }

    public String getPageSkin() {
        return this.pageSkin;
    }

    public void setPageSkin(String pageSkin) {
        this.pageSkin = pageSkin;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSpecialCharacter() {
        return this.specialCharacter;
    }

    public void setSpecialCharacter(String specialCharacter) {
        this.specialCharacter = specialCharacter;
    }

    public String getTodoVoicePrompt() {
        return this.todoVoicePrompt;
    }

    public void setTodoVoicePrompt(String todoVoicePrompt) {
        this.todoVoicePrompt = todoVoicePrompt;
    }

    public String getHelpManualOperation() {
        return this.helpManualOperation;
    }

    public void setHelpManualOperation(String helpManualOperation) {
        this.helpManualOperation = helpManualOperation;
    }

    public String getRollingNotice() {
        return this.rollingNotice;
    }

    public void setRollingNotice(String rollingNotice) {
        this.rollingNotice = rollingNotice;
    }

    public String getNotification() {
        return this.notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationAudio() {
        return this.notificationAudio;
    }

    public void setNotificationAudio(String notificationAudio) {
        this.notificationAudio = notificationAudio;
    }

    public String getSunwayOfficePreload() {
        return this.sunwayOfficePreload;
    }

    public void setSunwayOfficePreload(String sunwayOfficePreload) {
        this.sunwayOfficePreload = sunwayOfficePreload;
    }

    public String getElnPreload() {
        return this.elnPreload;
    }

    public void setElnPreload(String elnPreload) {
        this.elnPreload = elnPreload;
    }

    public String getFileOperationLog() {
        return this.fileOperationLog;
    }

    public void setFileOperationLog(String fileOperationLog) {
        this.fileOperationLog = fileOperationLog;
    }

    public String getIeAccessTip() {
        return this.ieAccessTip;
    }

    public void setIeAccessTip(String ieAccessTip) {
        this.ieAccessTip = ieAccessTip;
    }

    public String getFullTextSearchResult() {
        return this.fullTextSearchResult;
    }

    public void setFullTextSearchResult(String fullTextSearchResult) {
        this.fullTextSearchResult = fullTextSearchResult;
    }

    public Long getSpeedTestPeriod() {
        return this.speedTestPeriod;
    }

    public void setSpeedTestPeriod(Long speedTestPeriod) {
        this.speedTestPeriod = speedTestPeriod;
    }

    public String getInitPasswordPolicy() {
        return this.initPasswordPolicy;
    }

    public void setInitPasswordPolicy(String initPasswordPolicy) {
        this.initPasswordPolicy = initPasswordPolicy;
    }

    public String getDefaultPassword() {
        return this.defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getHelpCenter() {
        return this.helpCenter;
    }

    public void setHelpCenter(String helpCenter) {
        this.helpCenter = helpCenter;
    }

    public String getSystemVersion() {
        return this.systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getTodoDisplayCategory() {
        return this.todoDisplayCategory;
    }

    public void setTodoDisplayCategory(String todoDisplayCategory) {
        this.todoDisplayCategory = todoDisplayCategory;
    }

    public String getLicenseInfo() {
        return this.licenseInfo;
    }

    public void setLicenseInfo(String licenseInfo) {
        this.licenseInfo = licenseInfo;
    }
}
