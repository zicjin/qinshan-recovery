/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineRepresentation;
import com.sunwayworld.module.sys.menu.service.CoreMenuGroupService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/menu-groups"})
public interface CoreMenuGroupResource
extends GenericResource<CoreMenuGroupService, CoreMenuGroupBean, String>,
GenericSuspendableResource<CoreMenuGroupService, CoreMenuGroupBean, String>,
GenericChoosableResource<CoreMenuGroupService, CoreMenuGroupBean, String>,
GenericSelectableResource<CoreMenuGroupService, CoreMenuGroupBean, String> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/menu-group-lines"}, method={RequestMethod.POST})
    public void insertLines(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/menu-group-lines"}, method={RequestMethod.DELETE})
    public void deleteLines(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/menu-group-lines/tree-grid"}, method={RequestMethod.POST})
    public Page<CoreMenuGroupLineRepresentation> selectLineTreePagination(@PathVariable String var1, RestJsonWrapperBean var2);
}
