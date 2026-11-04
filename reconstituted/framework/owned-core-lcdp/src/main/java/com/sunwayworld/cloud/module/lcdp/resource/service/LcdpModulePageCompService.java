/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpModulePageCompService
extends GenericService<LcdpModulePageCompBean, String> {
    public void dealGridSroll(List<LcdpResourceHistoryBean> var1);

    public void dealGridBadgeCount(List<LcdpResourceHistoryBean> var1);

    public void dealGridShowCheckedNum(List<LcdpResourceHistoryBean> var1);

    public void dealHistoryGridCheckContinuous(List<LcdpResourceHistoryBean> var1);

    public void dealHistoryShowCheckedNum2Null(List<LcdpResourceHistoryBean> var1);
}
