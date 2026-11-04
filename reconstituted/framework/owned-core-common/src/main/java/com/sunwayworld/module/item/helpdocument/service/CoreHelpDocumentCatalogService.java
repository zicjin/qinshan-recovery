/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogDTO;
import java.util.List;

public interface CoreHelpDocumentCatalogService
extends GenericService<CoreHelpDocumentCatalogBean, Long> {
    public Page<CoreHelpDocumentBean> selectDocumentPaginationByHelpDocumentCatalogId(Long var1, RestJsonWrapperBean var2);

    public Long insertDocument(RestJsonWrapperBean var1);

    public void deleteDocument(RestJsonWrapperBean var1);

    public List<CoreHelpDocumentCatalogDTO> selectEditTree(String var1, RestJsonWrapperBean var2);

    public void drag(RestJsonWrapperBean var1);

    public void activate(RestJsonWrapperBean var1);

    public void deactivate(RestJsonWrapperBean var1);

    public Long getId();
}
