/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.resource;

import com.sunwayworld.cloud.module.item.document.bean.CoreDocumentBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/core/module/item/documents"})
public interface CoreDocumentResource {
    @RequestMapping(value={"/{id}"}, method={RequestMethod.GET})
    public CoreDocumentBean selectOneById(@PathVariable(value="id") Long var1);

    @RequestMapping(value={"/"}, method={RequestMethod.GET})
    public CoreDocumentBean selectOne(@RequestParam(value="table") String var1, @RequestParam(value="itemId") String var2, @RequestParam(value="documentType") String var3);

    @RequestMapping(value={"/{id}"}, method={RequestMethod.PUT})
    public void save(@PathVariable(value="id") Long var1, RestJsonWrapperBean var2);
}
