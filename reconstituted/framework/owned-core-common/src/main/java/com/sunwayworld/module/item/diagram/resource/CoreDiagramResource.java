/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.diagram.bean.CoreDiagramBean;
import com.sunwayworld.module.item.diagram.service.CoreDiagramService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/diagrams"})
public interface CoreDiagramResource
extends GenericResource<CoreDiagramService, CoreDiagramBean, Long> {
    public <ID> CoreDiagramBean selectOne(String var1, ID var2);

    @Override
    public void save(Long var1, RestJsonWrapperBean var2);
}
