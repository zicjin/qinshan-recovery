/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/password-policies"})
public interface CorePasswordPolicyResource
extends GenericResource<CorePasswordPolicyService, CorePasswordPolicyBean, Long>,
GenericSuspendableResource<CorePasswordPolicyService, CorePasswordPolicyBean, Long>,
GenericSelectableResource<CorePasswordPolicyService, CorePasswordPolicyBean, Long> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.GET})
    public CorePasswordPolicyBean selectMatchingPasswordPolicy();
}
