/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.resource.impl;

import com.sunwayworld.cloud.module.dms.bean.CoreFileDocumentBean;
import com.sunwayworld.cloud.module.dms.resource.CoreFileDocumentResource;
import com.sunwayworld.cloud.module.dms.service.CoreFileDocumentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u6587\u6863\u7ba1\u7406")
public class CoreFileDocumentResourceImpl
implements CoreFileDocumentResource {
    @Autowired
    private CoreFileDocumentService documentService;

    @Override
    @Log(value="\u901a\u8fc7\u6587\u4ef6\u5411ES\u6dfb\u52a0\u6587\u6863\u4fe1\u606f", type=LogType.INSERT)
    public Long insertByFile(@PathVariable(value="fileId") Long fileId, @RequestParam(value="category", required=false) String category) {
        return this.documentService.insertByFile(fileId, category);
    }

    @Override
    @Log(value="\u901a\u8fc7\u6587\u4ef6\u5411ES\u66f4\u65b0\u6587\u6863\u4fe1\u606f", type=LogType.UPDATE)
    public Long updateByFile(@PathVariable(value="fileId") Long fileId, @RequestParam(value="category", required=false) String category) {
        return this.documentService.updateByFile(fileId, category);
    }

    @Override
    @Log(value="\u901a\u8fc7\u6587\u4ef6\u5411ES\u5220\u9664\u6587\u6863\u4fe1\u606f", type=LogType.DELETE)
    public void deleteByFile(@PathVariable(value="fileId") Long fileId) {
        this.documentService.deleteByFile(fileId);
    }

    @Override
    @Log(value="\u5206\u9875\u67e5\u8be2\u6587\u6863\u4fe1\u606f", type=LogType.SELECT)
    public Page<CoreFileDocumentBean> selectPagination(RestJsonWrapperBean jsonWrapper) {
        return this.documentService.selectPagination(jsonWrapper.extractPageRequest(), jsonWrapper.getFilterValue("category"), jsonWrapper.getFilterValue("keywords"));
    }
}
