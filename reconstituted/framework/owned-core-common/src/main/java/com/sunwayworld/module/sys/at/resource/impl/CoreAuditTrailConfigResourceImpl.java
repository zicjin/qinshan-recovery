/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigTreeDTO;
import com.sunwayworld.module.sys.at.resource.CoreAuditTrailConfigResource;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class CoreAuditTrailConfigResourceImpl
implements CoreAuditTrailConfigResource {
    @Autowired
    private CoreAuditTrailConfigService configService;

    @Override
    public CoreAuditTrailConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u53ef\u5ba1\u8ba1\u8ddf\u8e2a\u7684\u914d\u7f6e\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/audit-trailable/tree-grid"}, method={RequestMethod.POST})
    public Page<CoreAuditTrailConfigTreeDTO> selectAuditTrailableTreePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditTrailableTreePagination(wrapper);
    }

    @Override
    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u7684\u914d\u7f6e\u660e\u7ec6\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/at-config-lines"}, method={RequestMethod.POST})
    public Page<CoreAuditTrailConfigLineBean> selectAuditTrailConfigLineListByConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditTrailConfigLineListByConfig(id, wrapper);
    }

    @Override
    @Log(value="\u5ba1\u8ba1", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/at-config-lines/action/audit"}, method={RequestMethod.PUT})
    public void audit(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().auditConfigLines(id, wrapper);
    }

    @Override
    @Log(value="\u53d6\u6d88\u5ba1\u8ba1", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/at-config-lines/action/cancel"}, method={RequestMethod.PUT})
    public void cancel(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().cancelConfigLines(id, wrapper);
    }
}
