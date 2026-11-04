/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.bean.CoreMenuIconDTO;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/menus"})
public interface CoreMenuResource
extends GenericResource<CoreMenuService, CoreMenuBean, String>,
GenericTreeResource<CoreMenuService, CoreMenuBean, String>,
GenericChoosableResource<CoreMenuService, CoreMenuBean, String> {
    @RequestMapping(value={"/edit-tree/{parentId}"}, method={RequestMethod.POST})
    public List<CoreMenuDTO> selectEditTree(String var1, RestJsonWrapperBean var2);

    @Override
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/{firstId}/{firstOrderNo}/swap/{secondId}/{secondOrderNo}"}, method={RequestMethod.PUT})
    public void swapOrderNo(String var1, Long var2, String var3, Long var4, RestJsonWrapperBean var5);

    @RequestMapping(value={"/tree-grid"}, method={RequestMethod.POST})
    public Page<CoreMenuDTO> selectTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/drag"}, method={RequestMethod.PUT})
    public void drag(RestJsonWrapperBean var1);

    @RequestMapping(value={"/icons/queries"}, method={RequestMethod.GET})
    public List<CoreMenuIconDTO> selectIconList();

    @RequestMapping(value={"/todo-icons/queries"}, method={RequestMethod.GET})
    public List<CoreMenuIconDTO> selectTodoIconList();

    @RequestMapping(value={"/published-menus/{id}"}, method={RequestMethod.GET})
    public CoreMenuBean selectPublishedMenuDetail(@PathVariable String var1);

    @RequestMapping(value={"/published-menus/{id}"}, method={RequestMethod.PUT})
    public void savePublishedMenu(@PathVariable String var1, RestJsonWrapperBean var2);
}
