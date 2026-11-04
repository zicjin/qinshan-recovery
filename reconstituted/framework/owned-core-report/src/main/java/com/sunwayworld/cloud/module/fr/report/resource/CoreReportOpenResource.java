/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/core/module/item/reports"})
public interface CoreReportOpenResource {
    @RequestMapping(value={"/action/export/{exportType}/{reportConfigLineId}"}, method={RequestMethod.POST})
    public String export(String var1, Long var2, String var3);
}
