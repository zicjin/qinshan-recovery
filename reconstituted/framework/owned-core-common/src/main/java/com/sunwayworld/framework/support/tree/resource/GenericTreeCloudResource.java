/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericTreeCloudResource<T extends Persistable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/tree/{parentId}"}, method={RequestMethod.POST})
    public ArrayList<? extends AbstractTreeNode> selectTree(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/pop-tree/{parentId}"}, method={RequestMethod.POST})
    public ArrayList<? extends AbstractTreeNode> selectPopTree(@PathVariable String var1, RestJsonWrapperBean var2);
}
