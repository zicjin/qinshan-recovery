/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/codes"})
public interface CoreCodeResource
extends GenericResource<CoreCodeService, CoreCodeBean, Long>,
GenericSuspendableResource<CoreCodeService, CoreCodeBean, Long>,
GenericSelectableResource<CoreCodeService, CoreCodeBean, Long>,
GenericChoosableResource<CoreCodeService, CoreCodeBean, Long> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);
}
