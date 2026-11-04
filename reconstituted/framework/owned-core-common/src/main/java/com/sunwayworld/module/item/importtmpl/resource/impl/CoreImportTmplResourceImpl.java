/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplConfigBean;
import com.sunwayworld.module.item.importtmpl.resource.CoreImportTmplResource;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5bfc\u5165\u6a21\u7248")
@RestController
@GikamBean
public class CoreImportTmplResourceImpl
implements CoreImportTmplResource {
    @Autowired
    private CoreImportTmplService coreImportTmplService;

    @Override
    public CoreImportTmplService getService() {
        return this.coreImportTmplService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5bfc\u5165\u6a21\u7248", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u521d\u59cb\u5316\u5bfc\u5165\u6a21\u677f\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/init-config"}, method={RequestMethod.POST})
    public void initTmplConfig(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().initTmplConfig(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5bfc\u5165\u6a21\u7248ID\u67e5\u8be2\u5bfc\u5165\u6a21\u677f\u914d\u7f6e", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/configs/queries"}, method={RequestMethod.POST})
    public Page<CoreImportTmplConfigBean> selectTmplConfigPaginationByImportTmplId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectTmplConfigPaginationByImportTmplId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5bfc\u5165\u6a21\u677f\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/configs"}, method={RequestMethod.POST})
    public Long insertTmplConfig(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertTmplConfig(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5bfc\u5165\u6a21\u677f\u914d\u7f6e", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/configs"}, method={RequestMethod.DELETE})
    public void deleteTmplConfig(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteTmplConfig(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u9644\u4ef6\u7f16\u7801\u5bfc\u5165\u6570\u636e", type=LogType.IMPORT)
    @RequestMapping(value={"/{importTmplId}/files/{id}/action/import"}, method={RequestMethod.POST})
    public JSONObject importData(@PathVariable Long importTmplId, @PathVariable Long id) {
        return this.getService().importData(importTmplId, id);
    }
}
