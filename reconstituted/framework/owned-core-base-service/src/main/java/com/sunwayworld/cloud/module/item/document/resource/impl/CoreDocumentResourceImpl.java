/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.resource.impl;

import com.sunwayworld.cloud.module.item.document.bean.CoreDocumentBean;
import com.sunwayworld.cloud.module.item.document.resource.CoreDocumentResource;
import com.sunwayworld.cloud.module.item.document.service.CoreDocumentService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6587\u6863\u8bf4\u660e")
@RestController
@GikamBean
public class CoreDocumentResourceImpl
implements CoreDocumentResource {
    @Autowired
    private CoreDocumentService documentService;

    @Override
    @Log(value="\u67e5\u8be2", type=LogType.SELECT)
    public CoreDocumentBean selectOne(@RequestParam String table, @RequestParam String itemId, @RequestParam String documentType) {
        return this.documentService.selectOne(table, itemId, documentType);
    }

    @Override
    @Log(value="\u67e5\u8be2", type=LogType.SELECT)
    public CoreDocumentBean selectOneById(@PathVariable Long id) {
        return this.documentService.selectOne(id);
    }

    @Override
    @Log(value="\u4fdd\u5b58", type=LogType.UPDATE)
    public void save(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.documentService.save(wrapper);
    }
}
