/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcedocument.resource;

import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/resource-documents"})
public interface LcdpResourceDocumentResource {
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public Page<LcdpResourceDocumentBean> selectPagination(RestJsonWrapperBean var1);
}
