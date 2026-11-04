/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.addomain.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/ad-domains"})
public interface CoreAdDomainResource {
    @RequestMapping(value={"/ad-domain-users"}, method={RequestMethod.POST})
    public void insertAdDomainUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/ad-domain-users/{id}/action/validate-unique", "/ad-domain-users/{id}/action/validate-unique/{columnName}/{columnValue}", "/ad-domain-users/action/validate-unique/{columnName}/{columnValue}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateAdDomainUserUnique(@PathVariable(required=false) String var1, @PathVariable(required=false) String var2, @PathVariable(required=false) String var3);
}
