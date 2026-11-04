/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.resource.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.resource.LimsReportTemplateResource;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62a5\u544a\u6a21\u677f\u8868")
@RestController
@GikamBean
public class LimsReportTemplateResourceImpl
implements LimsReportTemplateResource {
    @Autowired
    private LimsReportTemplateService limsReportTemplateService;

    @Override
    public LimsReportTemplateService getService() {
        return this.limsReportTemplateService;
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u544a\u6a21\u677f\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u62a5\u544a\u6a21\u677f\u8868ID\u67e5\u8be2\u62a5\u544a\u6a21\u677f\u6570\u636e\u6e90\u914d\u7f6e\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/template-sources/queries"}, method={RequestMethod.POST})
    public Page<LimsReportTemplateSourceBean> selectTemplateSourcePaginationByReportTemplateId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectTemplateSourcePaginationByReportTemplateId(id, wrapper);
    }

    @Override
    @GetMapping(value={"/sources/{id}"})
    public LimsReportTemplateSourceBean selectTemplateSourceDetail(@PathVariable Long id) {
        return this.getService().selectTemplateSourceDetail(id);
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u544a\u6a21\u677f\u6570\u636e\u6e90\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/template-sources"}, method={RequestMethod.POST})
    public Long insertTemplateSource(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertTemplateSource(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u62a5\u544a\u6a21\u677f\u6570\u636e\u6e90\u914d\u7f6e", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/template-sources"}, method={RequestMethod.DELETE})
    public void deleteTemplateSource(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteTemplateSource(id, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1SQL", type=LogType.VALIDATE)
    @RequestMapping(value={"/{id}/action/validate-sql", "/{id}/action/validate-sql/{columnName}/{columnValue}", "/action/validate-sql/{columnName}/{columnValue}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateSql(@PathVariable(required=false) Long id, @PathVariable String columnName, @PathVariable String columnValue) {
        return this.getService().validateSql(id, columnName, columnValue);
    }

    @Override
    @Log(value="\u9a8c\u8bc1SQL", type=LogType.VALIDATE)
    @PostMapping(value={"/action/validate-sql"})
    public RestValidationResultBean validateSql(RestJsonWrapperBean wrapperBean) {
        return this.getService().validateSql(wrapperBean);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6a21\u677f\u6570\u636e\u6e90\u5217\u914d\u7f6e", type=LogType.SELECT)
    @RequestMapping(value={"/sources/{id}/template-columns/queries"}, method={RequestMethod.POST})
    public Page<LimsReportTemplateColumnBean> selectTemplateColumnPaginationByReportTemplateId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectTemplateColumnPaginationByTemplateSourceId(id, wrapper);
    }

    @Override
    @Log(value="\u9884\u89c8\u751f\u6210\u62a5\u544a", type=LogType.CONTROLLER)
    @PostMapping(value={"/{id}/action/generator"})
    public String generatorReport(@PathVariable Long id, RestJsonWrapperBean wrapperBean) {
        return this.getService().generatorReport(id, wrapperBean);
    }

    @Override
    @Log(value="\u751f\u6210\u62a5\u544a", type=LogType.CONTROLLER)
    @PostMapping(value={"/{id}/{dbTable}/action/generator"})
    public CoreFileBean generatorReport(@PathVariable Long id, @PathVariable String dbTable, RestJsonWrapperBean wrapperBean) {
        return this.getService().generatorReport(id, dbTable, wrapperBean);
    }

    @Override
    @Log(value="\u5237\u65b0\u5217\u914d\u7f6e", type=LogType.UPDATE)
    @PostMapping(value={"/sources/{id}/columns/action/refresh"})
    public void refreshSourceColumn(@PathVariable Long id, RestJsonWrapperBean wrapperBean) {
        this.getService().refreshSourceColumn(id, wrapperBean);
    }

    @Override
    @Log(value="\u590d\u5236\u6a21\u677f", type=LogType.INSERT)
    @RequestMapping(value={"/action/copy"}, method={RequestMethod.POST})
    public void copyTemplate(RestJsonWrapperBean wrapperBean) {
        this.getService().copyTemplate(wrapperBean);
    }

    @Override
    @PostMapping(value={"/action/export-zip"})
    @Log(value="\u62a5\u544a\u6a21\u677f\u6587\u4ef6\u6253\u5305\u5bfc\u51fa", type=LogType.EXPORT)
    public String exportDataZip(RestJsonWrapperBean wrapperBean) {
        return this.getService().exportDataZip(wrapperBean);
    }

    @Override
    @Log(value="\u590d\u5236\u6a21\u677f(\u5e26\u6a21\u677f\u6587\u4ef6)", type=LogType.INSERT)
    @RequestMapping(value={"/action/copy-with-file"}, method={RequestMethod.POST})
    public Long copyTemplateWithFile(RestJsonWrapperBean wrapperBean) {
        return this.getService().copyTemplateWithFile(wrapperBean);
    }
}
