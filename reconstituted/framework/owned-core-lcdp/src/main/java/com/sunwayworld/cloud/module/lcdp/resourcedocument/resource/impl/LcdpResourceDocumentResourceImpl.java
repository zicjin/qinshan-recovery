/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcedocument.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.resource.LcdpResourceDocumentResource;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.support.LuceneSearchManager;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u8d44\u6e90\u6587\u4ef6\u5168\u6587\u68c0\u7d22")
public class LcdpResourceDocumentResourceImpl
implements LcdpResourceDocumentResource {
    @Override
    public Page<LcdpResourceDocumentBean> selectPagination(RestJsonWrapperBean jsonWrapper) {
        return LuceneSearchManager.searchPagination(jsonWrapper);
    }
}
