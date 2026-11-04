/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.resource.impl;

import com.sunwayworld.cloud.module.fr.report.resource.CoreReportOpenResource;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62a5\u8868\u5bfc\u51fa")
@RestController
@GikamBean
public class CoreReportOpenResourceImpl
implements CoreReportOpenResource {
    @Autowired
    private CoreReportService coreReportService;

    @Override
    public String export(@PathVariable String exportType, @PathVariable(required=false) Long reportConfigLineId, @RequestBody String json) {
        return this.coreReportService.export(exportType, reportConfigLineId, json);
    }
}
