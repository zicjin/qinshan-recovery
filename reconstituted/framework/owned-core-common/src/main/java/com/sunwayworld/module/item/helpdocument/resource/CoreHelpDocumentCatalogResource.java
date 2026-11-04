/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogBean;
import com.sunwayworld.module.item.helpdocument.bean.CoreHelpDocumentCatalogDTO;
import com.sunwayworld.module.item.helpdocument.service.CoreHelpDocumentCatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/help-document-catalogs"})
public interface CoreHelpDocumentCatalogResource
extends GenericResource<CoreHelpDocumentCatalogService, CoreHelpDocumentCatalogBean, Long> {
    @RequestMapping(value={"/{id}/documents/queries"}, method={RequestMethod.POST})
    public Page<CoreHelpDocumentBean> selectDocumentPaginationByHelpDocumentCatalogId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/documents"}, method={RequestMethod.POST})
    public Long insertDocument(RestJsonWrapperBean var1);

    @RequestMapping(value={"/documents"}, method={RequestMethod.DELETE})
    public void deleteDocument(RestJsonWrapperBean var1);

    @RequestMapping(value={"/edit-tree/{parentId}"}, method={RequestMethod.POST})
    public List<CoreHelpDocumentCatalogDTO> selectEditTree(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/drag"}, method={RequestMethod.PUT})
    public void drag(RestJsonWrapperBean var1);

    @RequestMapping(value={"/documents/action/activate"}, method={RequestMethod.PUT})
    public void activate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/documents/action/deactivate"}, method={RequestMethod.PUT})
    public void deactivate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/document-id"}, method={RequestMethod.GET})
    public Long getId();
}
