/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeTmplBean;
import com.sunwayworld.cloud.module.office.resource.CoreOfficeTmplResource;
import com.sunwayworld.cloud.module.office.service.CoreOfficeTmplService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62a5\u544a\u6a21\u677f\u8868")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/office/tmpls"})
public class CoreOfficeTmplResourceImpl
implements CoreOfficeTmplResource,
AbstractGenericResource<CoreOfficeTmplService, CoreOfficeTmplBean, Long>,
AbstractGenericActivatableResource<CoreOfficeTmplService, CoreOfficeTmplBean, Long>,
AbstractGenericChoosableResource<CoreOfficeTmplService, CoreOfficeTmplBean, Long> {
    @Autowired
    private CoreOfficeTmplService coreOfficeTmplService;

    @Override
    public CoreOfficeTmplService getService() {
        return this.coreOfficeTmplService;
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u544a\u6a21\u677f\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6a21\u677fID\u751f\u6210\u5b9e\u4f8b", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/generate-instance"}, method={RequestMethod.POST})
    public String buildReport(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().buildReport(id, wrapper, true);
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u751f\u6210\u5b9e\u4f8b", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/test-generate-instance"}, method={RequestMethod.POST})
    public String testBuildReport(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().buildReport(id, wrapper, false);
    }

    @Override
    @Log(value="\u6dfb\u52a0\u7a7a\u767d\u6a21\u677f", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/insert-blank-document"}, method={RequestMethod.POST})
    public Long insertBlankDocument(@PathVariable Long id) {
        return this.getService().insertBlankDocument(id);
    }

    @Override
    @Log(value="\u6839\u636e\u6a21\u677fID\u83b7\u53d6\u6a21\u677f\u6587\u4ef6\u6d4b\u8bd5\u53c2\u6570", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/test-form-field/queries"}, method={RequestMethod.GET})
    public String getTestFormFieldByTmplId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().getTestFormFieldByTmplId(id, wrapper);
    }

    @Override
    @Log(value="\u6e05\u7a7a\u6a21\u677f\u6587\u4ef6\u5173\u8054\u6570\u636e\u6e90\u7f13\u5b58", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/clear-bind-ds"}, method={RequestMethod.POST})
    public void clearBindDs(@PathVariable Long id) {
        this.getService().clearBindDs(id);
    }
}
