/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.resource;

import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/pdf/pdf-documents"})
public interface CorePdfDocumentResource
extends GenericCloudResource<CorePdfDocumentBean, Long> {
    @RequestMapping(value={"/files/{fileId}/documents"}, method={RequestMethod.POST})
    public void insertPdfDocumentInfoByFileId(@PathVariable Long var1);

    @RequestMapping(value={"/files/{fileId}/documents/queries"}, method={RequestMethod.POST})
    public Pagination<CorePdfDocumentDTO> selectPdfDocumentPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/files/{fileId}/documents"}, method={RequestMethod.PUT})
    public void savePdfDocument(RestJsonWrapperBean var1);
}
