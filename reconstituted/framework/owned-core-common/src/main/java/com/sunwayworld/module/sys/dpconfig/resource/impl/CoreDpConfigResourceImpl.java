/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigImportDataDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigLineBean;
import com.sunwayworld.module.sys.dpconfig.resource.CoreDpConfigResource;
import com.sunwayworld.module.sys.dpconfig.service.CoreDpConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6570\u636e\u6cf5\u914d\u7f6e")
@RestController
@GikamBean
public class CoreDpConfigResourceImpl
implements CoreDpConfigResource {
    @Autowired
    private CoreDpConfigService dpConfigService;

    @Override
    public CoreDpConfigService getService() {
        return this.dpConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6570\u636e\u6cf5\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6570\u636e\u6cf5\u914d\u7f6eID\u67e5\u8be2\u6570\u636e\u6cf5\u914d\u7f6e\u660e\u7ec6", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/lines/queries"}, method={RequestMethod.POST})
    public Page<CoreDpConfigLineBean> selectConfigLinePaginationByDpConfigId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectConfigLinePaginationByDpConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6570\u636e\u6cf5\u914d\u7f6e\u660e\u7ec6", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.POST})
    public Long insertConfigLine(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertConfigLine(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6570\u636e\u6cf5\u914d\u7f6e\u660e\u7ec6", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.DELETE})
    public void deleteConfigLine(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteConfigLine(id, wrapper);
    }

    @Override
    @Log(value="\u5bfc\u51fa", type=LogType.EXPORT)
    @RequestMapping(value={"/action/export"}, method={RequestMethod.POST})
    public String export(RestJsonWrapperBean wrapper) {
        return this.getService().export(wrapper);
    }

    @Override
    @Log(value="\u5bfc\u5165\u6821\u9a8c", type=LogType.VALIDATE)
    @RequestMapping(value={"/files/{fileId}/action/import-validate"}, method={RequestMethod.POST})
    public List<CoreDpConfigImportDataDTO> importValidate(@PathVariable Long fileId, RestJsonWrapperBean wrapper) {
        return this.getService().importValidate(fileId, wrapper);
    }

    @Override
    @Log(value="\u5bfc\u5165", type=LogType.IMPORT)
    @RequestMapping(value={"/files/{fileId}/action/import"}, method={RequestMethod.POST})
    public CoreFileImportResultDTO importData(@PathVariable Long fileId, RestJsonWrapperBean wrapper) {
        return this.getService().importData(fileId, wrapper);
    }
}
