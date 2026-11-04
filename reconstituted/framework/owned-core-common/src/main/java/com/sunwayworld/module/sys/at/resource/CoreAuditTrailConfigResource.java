/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigTreeDTO;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/at-configs"})
public interface CoreAuditTrailConfigResource
extends GenericResource<CoreAuditTrailConfigService, CoreAuditTrailConfigBean, String> {
    public Page<CoreAuditTrailConfigTreeDTO> selectAuditTrailableTreePagination(RestJsonWrapperBean var1);

    public Page<CoreAuditTrailConfigLineBean> selectAuditTrailConfigLineListByConfig(@PathVariable String var1, RestJsonWrapperBean var2);

    public void audit(@PathVariable String var1, RestJsonWrapperBean var2);

    public void cancel(@PathVariable String var1, RestJsonWrapperBean var2);
}
