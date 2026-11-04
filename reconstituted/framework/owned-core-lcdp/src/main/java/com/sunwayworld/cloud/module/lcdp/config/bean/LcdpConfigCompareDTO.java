/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.bean;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpConfigCompareDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 3522477116015765499L;
    private LcdpGlobalConfigBean currentVersionHistory;
    private LcdpGlobalConfigBean previousVersionHistory;
    private List<LcdpGlobalConfigBean> historyList;

    public LcdpGlobalConfigBean getCurrentVersionHistory() {
        return this.currentVersionHistory;
    }

    public void setCurrentVersionHistory(LcdpGlobalConfigBean currentVersionHistory) {
        this.currentVersionHistory = currentVersionHistory;
    }

    public LcdpGlobalConfigBean getPreviousVersionHistory() {
        return this.previousVersionHistory;
    }

    public void setPreviousVersionHistory(LcdpGlobalConfigBean previousVersionHistory) {
        this.previousVersionHistory = previousVersionHistory;
    }

    public List<LcdpGlobalConfigBean> getHistoryList() {
        return this.historyList;
    }

    public void setHistoryList(List<LcdpGlobalConfigBean> historyList) {
        this.historyList = historyList;
    }
}
