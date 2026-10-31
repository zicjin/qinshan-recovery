// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.report.report.resource;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/open/emc/module/bp/report/reports"})
public interface PfOpenReportResource {
    public EmcReportBean selectTestConclusion(Long var1);
}
