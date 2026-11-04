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
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericTreeResource<S extends GenericTreeService<T, ID>, T extends Persistable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u67e5\u8be2\u6811", type=LogType.SELECT)
    @RequestMapping(value={"/tree/{parentId}"}, method={RequestMethod.POST})
    default public List<? extends AbstractTreeNode> selectTree(@PathVariable String parentId, RestJsonWrapperBean jsonWrapper) {
        MapperParameter parameter = jsonWrapper.extractMapFilter();
        parameter.put("parentId", parentId);
        return this.getService().selectTree(parameter);
    }

    @Log(value="\u5f39\u51fa\u9875\u9762\u67e5\u8be2\u6811", type=LogType.SELECT)
    @RequestMapping(value={"/pop-tree/{parentId}"}, method={RequestMethod.POST})
    default public List<? extends AbstractTreeNode> selectPopTree(@PathVariable String parentId, RestJsonWrapperBean jsonWrapper) {
        MapperParameter parameter = jsonWrapper.extractMapFilter();
        parameter.put("parentId", parentId);
        parameter.setSuspendedFlag("0");
        return this.getService().selectTree(parameter);
    }
}
