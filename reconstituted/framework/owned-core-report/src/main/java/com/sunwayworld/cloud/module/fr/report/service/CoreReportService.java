/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.service;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.framework.data.Pair;
import java.util.List;

public interface CoreReportService {
    public <T> List<Pair<CoreReportConfigLineBean, String>> exportByConfigId(String var1, String var2, T var3);

    public <T> Pair<CoreReportConfigLineBean, String> exportByConfigLineId(String var1, String var2, Long var3, T var4);

    public String export(String var1, Long var2, String var3);
}
