/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.resource.impl;

import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentDTO;
import com.sunwayworld.cloud.module.pdf.resource.CorePdfDocumentResource;
import com.sunwayworld.cloud.module.pdf.service.CorePdfDocumentService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="PDF\u6587\u4ef6\u89e3\u6790")
@RestController
@GikamBean
public class CorePdfDocumentResourceImpl
implements CorePdfDocumentResource,
AbstractGenericResource<CorePdfDocumentService, CorePdfDocumentBean, Long> {
    @Autowired
    private CorePdfDocumentService corePdfDocumentService;

    @Override
    public CorePdfDocumentService getService() {
        return this.corePdfDocumentService;
    }

    @Override
    @Log(value="PDF\u6279\u6ce8\u9884\u89c8\u6587\u4ef6\u751f\u6210", type=LogType.INSERT)
    public void insertPdfDocumentInfoByFileId(Long fileId) {
        this.getService().createPdfDocumentInfoByFileId(fileId);
    }

    @Override
    @Log(value="PDF\u6279\u6ce8\u9884\u89c8\u9875\u9762", type=LogType.SELECT)
    public Pagination<CorePdfDocumentDTO> selectPdfDocumentPagination(Long fileId, RestJsonWrapperBean wrapper) {
        return this.getService().selectCorePdfDocumentPagination(fileId, wrapper);
    }

    @Override
    @Log(value="PDF\u6279\u6ce8\u6846\u9009\u533a\u57df\u5b58\u50a8", type=LogType.UPDATE)
    public void savePdfDocument(RestJsonWrapperBean wrapper) {
        this.getService().savePdfDocumentInfo(wrapper);
    }
}
