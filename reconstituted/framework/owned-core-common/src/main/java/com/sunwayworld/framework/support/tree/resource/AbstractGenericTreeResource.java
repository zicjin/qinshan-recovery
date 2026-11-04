/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.support.tree.resource.GenericTreeCloudResource;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import java.io.Serializable;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PathVariable;

public interface AbstractGenericTreeResource<S extends GenericTreeService<T, ID>, T extends Persistable<ID>, ID extends Serializable>
extends GenericTreeCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u67e5\u8be2\u6811", type=LogType.SELECT)
    default public ArrayList<? extends AbstractTreeNode> selectTree(@PathVariable String parentId, RestJsonWrapperBean jsonWrapper) {
        MapperParameter parameter = jsonWrapper.extractMapFilter();
        parameter.put("parentId", parentId);
        return (ArrayList)this.getService().selectTree(parameter);
    }

    @Override
    @Log(value="\u5f39\u51fa\u9875\u9762\u67e5\u8be2\u6811", type=LogType.SELECT)
    default public ArrayList<? extends AbstractTreeNode> selectPopTree(@PathVariable String parentId, RestJsonWrapperBean jsonWrapper) {
        MapperParameter parameter = jsonWrapper.extractMapFilter();
        parameter.put("parentId", parentId);
        parameter.setSuspendedFlag("0");
        return (ArrayList)this.getService().selectTree(parameter);
    }
}
