/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogDTO;
import com.sunwayworld.module.item.helpdocument.resource.CoreHelpDocumentCatalogResource;
import com.sunwayworld.module.item.helpdocument.service.CoreHelpDocumentCatalogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5e2e\u52a9\u6587\u6863\u76ee\u5f55")
@RestController
@GikamBean
public class CoreHelpDocumentCatalogResourceImpl
implements CoreHelpDocumentCatalogResource {
    @Autowired
    private CoreHelpDocumentCatalogService coreHelpDocumentCatalogService;

    @Override
    public CoreHelpDocumentCatalogService getService() {
        return this.coreHelpDocumentCatalogService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5e2e\u52a9\u6587\u6863\u76ee\u5f55", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5e2e\u52a9\u6587\u6863\u76ee\u5f55ID\u67e5\u8be2\u5e2e\u52a9\u6587\u6863", type=LogType.SELECT)
    public Page<CoreHelpDocumentBean> selectDocumentPaginationByHelpDocumentCatalogId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectDocumentPaginationByHelpDocumentCatalogId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5e2e\u52a9\u6587\u6863", type=LogType.INSERT)
    public Long insertDocument(RestJsonWrapperBean wrapper) {
        return this.getService().insertDocument(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5e2e\u52a9\u6587\u6863", type=LogType.DELETE)
    public void deleteDocument(RestJsonWrapperBean wrapper) {
        this.getService().deleteDocument(wrapper);
    }

    @Override
    @Log(value="\u67e5\u770b\u5b8c\u6574\u6811", type=LogType.SELECT)
    public List<CoreHelpDocumentCatalogDTO> selectEditTree(String parentId, RestJsonWrapperBean wrapper) {
        return this.getService().selectEditTree(parentId, wrapper);
    }

    @Override
    @Log(value="\u62d6\u62fd\u6539\u53d8\u987a\u5e8f", type=LogType.UPDATE)
    public void drag(RestJsonWrapperBean wrapper) {
        this.getService().drag(wrapper);
    }

    @Override
    @Log(value="\u5e2e\u52a9\u6587\u6863\u542f\u7528", type=LogType.ACTIVATE)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Override
    @Log(value="\u5e2e\u52a9\u6587\u6863\u505c\u7528", type=LogType.UPDATE)
    public void deactivate(RestJsonWrapperBean wrapper) {
        this.getService().deactivate(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5e2e\u52a9\u6587\u6863\u4e3b\u952e", type=LogType.UPDATE)
    public Long getId() {
        return this.getService().getId();
    }
}
