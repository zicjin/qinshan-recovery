/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpResourceCompareDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 7270427410582349717L;
    private LcdpResourceHistoryBean currentVersionHistory;
    private List<LcdpModulePageCompBean> currentModulePageCompList;
    private LcdpResourceHistoryBean previousVersionHistory;
    private List<LcdpModulePageCompBean> previousModulePageCompList;
    private List<LcdpResourceHistoryBean> historyList;
    private LcdpTableBean currentTable;
    private LcdpTableBean previousTable;
    private LcdpViewBean currentView;
    private LcdpViewBean previousView;
    private LcdpGlobalConfigBean currentGlobalConfigHistory;
    private LcdpGlobalConfigBean previousGlobalConfigHistory;

    public LcdpResourceHistoryBean getCurrentVersionHistory() {
        return this.currentVersionHistory;
    }

    public void setCurrentVersionHistory(LcdpResourceHistoryBean currentVersionHistory) {
        this.currentVersionHistory = currentVersionHistory;
    }

    public LcdpResourceHistoryBean getPreviousVersionHistory() {
        return this.previousVersionHistory;
    }

    public void setPreviousVersionHistory(LcdpResourceHistoryBean previousVersionHistory) {
        this.previousVersionHistory = previousVersionHistory;
    }

    public List<LcdpModulePageCompBean> getCurrentModulePageCompList() {
        return this.currentModulePageCompList;
    }

    public void setCurrentModulePageCompList(List<LcdpModulePageCompBean> currentModulePageCompList) {
        this.currentModulePageCompList = currentModulePageCompList;
    }

    public List<LcdpModulePageCompBean> getPreviousModulePageCompList() {
        return this.previousModulePageCompList;
    }

    public void setPreviousModulePageCompList(List<LcdpModulePageCompBean> previousModulePageCompList) {
        this.previousModulePageCompList = previousModulePageCompList;
    }

    public List<LcdpResourceHistoryBean> getHistoryList() {
        return this.historyList;
    }

    public void setHistoryList(List<LcdpResourceHistoryBean> historyList) {
        this.historyList = historyList;
    }

    public LcdpTableBean getCurrentTable() {
        return this.currentTable;
    }

    public void setCurrentTable(LcdpTableBean currentTable) {
        this.currentTable = currentTable;
    }

    public LcdpTableBean getPreviousTable() {
        return this.previousTable;
    }

    public void setPreviousTable(LcdpTableBean previousTable) {
        this.previousTable = previousTable;
    }

    public LcdpViewBean getCurrentView() {
        return this.currentView;
    }

    public void setCurrentView(LcdpViewBean currentView) {
        this.currentView = currentView;
    }

    public LcdpViewBean getPreviousView() {
        return this.previousView;
    }

    public void setPreviousView(LcdpViewBean previousView) {
        this.previousView = previousView;
    }

    public LcdpGlobalConfigBean getCurrentGlobalConfigHistory() {
        return this.currentGlobalConfigHistory;
    }

    public void setCurrentGlobalConfigHistory(LcdpGlobalConfigBean currentGlobalConfigHistory) {
        this.currentGlobalConfigHistory = currentGlobalConfigHistory;
    }

    public LcdpGlobalConfigBean getPreviousGlobalConfigHistory() {
        return this.previousGlobalConfigHistory;
    }

    public void setPreviousGlobalConfigHistory(LcdpGlobalConfigBean previousGlobalConfigHistory) {
        this.previousGlobalConfigHistory = previousGlobalConfigHistory;
    }
}
