package com.sunwayworld.emc.module.bp.info.reporttmpl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.resource.EmcReportTmplResource;
import com.sunwayworld.emc.module.bp.info.reporttmpl.service.EmcReportTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("例行上报模板配置表")
@RestController
@GikamBean
public class EmcReportTmplResourceImpl implements EmcReportTmplResource {
    @Autowired
    private EmcReportTmplService reportTmplService;

    @Override
    public EmcReportTmplService getService() {
        return reportTmplService;
    }

    @Log(value = "新增例行上报模板", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "复制模板到", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/copyto", method = RequestMethod.POST)
    public void copyTo(@PathVariable String id, RestJsonWrapperBean wrapper) {
        reportTmplService.copyTo(id, wrapper);

    }

    @Override
    @Log(value = "复制配置", type = LogType.INSERT)
    @RequestMapping(value = "/copy-line", method = RequestMethod.POST)
    public void copyLine(RestJsonWrapperBean wrapper) {
        reportTmplService.copyLine(wrapper);
    }

    @Override
    @Log(value = "添加配置", type = LogType.INSERT)
    @RequestMapping(value = "/{reportTmplId}/lines", method = RequestMethod.POST)
    public Long insertLine(@PathVariable Long reportTmplId, RestJsonWrapperBean wrapper) {
        return reportTmplService.insertLine(reportTmplId, wrapper);
    }

    @Override
    @Log(value = "通过模板id查询配置", type = LogType.SELECT)
    @RequestMapping(value = "/{reportTmplId}/queries", method = RequestMethod.POST)
    public Page<EmcReportTmplLineBean> selectLinePagation(@PathVariable Long reportTmplId, RestJsonWrapperBean wrapper) {
        return reportTmplService.selectLinePagation(reportTmplId, wrapper);
    }

    @Override
    @Log(value = "删除配置", type = LogType.SELECT)
    @RequestMapping(value = "/lines", method = RequestMethod.DELETE)
    public void deleteLine(RestJsonWrapperBean wrapper) {
        reportTmplService.deleteLine(wrapper);
    }

}
