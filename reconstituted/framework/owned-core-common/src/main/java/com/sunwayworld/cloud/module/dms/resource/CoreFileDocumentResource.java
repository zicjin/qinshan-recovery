/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.resource;

import com.sunwayworld.cloud.module.dms.bean.CoreFileDocumentBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/dms/documents"})
public interface CoreFileDocumentResource {
    @RequestMapping(value={"/from-files/{fileId}"}, method={RequestMethod.POST})
    public Long insertByFile(@PathVariable(value="fileId") Long var1, @RequestParam(value="category", required=false) String var2);

    @RequestMapping(value={"/from-files/{fileId}"}, method={RequestMethod.PUT})
    public Long updateByFile(@PathVariable(value="fileId") Long var1, @RequestParam(value="category", required=false) String var2);

    @RequestMapping(value={"/from-files/{fileId}"}, method={RequestMethod.DELETE})
    public void deleteByFile(@PathVariable(value="fileId") Long var1);

    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public Page<CoreFileDocumentBean> selectPagination(RestJsonWrapperBean var1);
}
