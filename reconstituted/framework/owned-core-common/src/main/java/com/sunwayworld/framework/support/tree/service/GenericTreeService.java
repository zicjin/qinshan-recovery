/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree.service;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.io.Serializable;
import java.util.List;

public interface GenericTreeService<T extends Persistable<ID>, ID extends Serializable> {
    public List<? extends AbstractTreeNode> selectTree(MapperParameter var1);
}
