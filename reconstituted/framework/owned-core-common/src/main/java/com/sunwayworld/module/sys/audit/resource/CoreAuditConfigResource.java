/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/audit/audit-configs"})
public interface CoreAuditConfigResource
extends GenericResource<CoreAuditConfigService, CoreAuditConfigBean, String>,
GenericSelectableResource<CoreAuditConfigService, CoreAuditConfigBean, String> {
    @RequestMapping(value={"/action/init"}, method={RequestMethod.PUT})
    public void init();

    @RequestMapping(value={"/{id}/config-columns"}, method={RequestMethod.POST})
    public Page<CoreAuditConfigColumnBean> selectConfigColumnPaginationByConfigId(@PathVariable String var1);

    @RequestMapping(value={"/{id}/config-columns/action/init"}, method={RequestMethod.PUT})
    public void initConfigColumn(@PathVariable String var1);

    @RequestMapping(value={"/{id}/config-columns/action/enable-audit"}, method={RequestMethod.PUT})
    public void enableConfigColumn(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/config-columns/action/disable-audit"}, method={RequestMethod.PUT})
    public void disableConfigColumn(@PathVariable String var1, RestJsonWrapperBean var2);
}
