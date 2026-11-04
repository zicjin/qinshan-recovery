/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.diagram.bean.CoreDiagramBean;

public interface CoreDiagramService
extends GenericService<CoreDiagramBean, Long> {
    public <ID> void insert(String var1, ID var2, String var3);

    public <ID> void save(String var1, ID var2, String var3);

    public CoreDiagramBean selectOne(Long var1);

    public <ID> CoreDiagramBean selectOne(String var1, ID var2);
}
