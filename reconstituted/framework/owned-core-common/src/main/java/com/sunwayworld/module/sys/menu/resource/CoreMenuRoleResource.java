/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.matrix.resource.GenericMatrixResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/menu-roles"})
public interface CoreMenuRoleResource
extends GenericMatrixResource {
    @RequestMapping(value={"/instant"}, method={RequestMethod.PUT})
    public void instantSave(RestJsonWrapperBean var1);
}
