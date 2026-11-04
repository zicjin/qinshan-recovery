/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.service;

import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CorePdfDocumentService
extends GenericService<CorePdfDocumentBean, Long> {
    public void createPdfDocumentInfoByFileId(Long var1);

    public Pagination<CorePdfDocumentDTO> selectCorePdfDocumentPagination(Long var1, RestJsonWrapperBean var2);

    public void savePdfDocumentInfo(RestJsonWrapperBean var1);

    public void deleteCorePdfDocumentByFileId(Long var1);
}
