// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.report.report.resource.impl;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.resource.PfOpenReportResource;
import com.sunwayworld.emc.module.bp.report.report.service.PfReportService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class PfOpenReportResourceImpl
implements PfOpenReportResource {
    @Autowired
    private PfReportService reportService;

    @Override
    @RequestMapping(value={"/select-test-conclusion/{reportId}"}, method={RequestMethod.GET})
    public EmcReportBean selectTestConclusion(@PathVariable Long reportId) {
        return (EmcReportBean)this.reportService.selectFirstByFilter(SearchFilter.instance().match("id", reportId).filter(MatchPattern.SEQ), new Order[0]);
    }

    @RequestMapping(value={"/select-security-test-conclusion/{reportNo}"}, method={RequestMethod.GET})
    public EmcReportBean selectTestConclusion(@PathVariable String reportNo) {
        return (EmcReportBean)this.reportService.selectFirstByFilter(SearchFilter.instance().match("reportNo", reportNo).filter(MatchPattern.SEQ), new Order[0]);
    }
}
