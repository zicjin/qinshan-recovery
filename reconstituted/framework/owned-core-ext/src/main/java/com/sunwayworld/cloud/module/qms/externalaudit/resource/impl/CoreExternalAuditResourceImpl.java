/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.resource.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.externalaudit.resource.CoreExternalAuditResource;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5916\u90e8\u8bc4\u5ba1")
@RestController
@GikamBean
public class CoreExternalAuditResourceImpl
implements CoreExternalAuditResource,
AbstractGenericResource<CoreExternalAuditService, CoreExternalAuditBean, Long>,
GenericAuditableResource<CoreExternalAuditService, CoreExternalAuditBean, Long>,
GenericChoosableResource<CoreExternalAuditService, CoreExternalAuditBean, Long> {
    @Autowired
    private CoreExternalAuditService coreExternalAuditService;

    @Override
    public CoreExternalAuditService getService() {
        return this.coreExternalAuditService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5916\u90e8\u8bc4\u5ba1", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5916\u90e8\u8bc4\u5ba1ID\u67e5\u8be2\u5916\u90e8\u8bc4\u5ba1-\u5ba1\u6838\u7ec4", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/audit-groups/queries"}, method={RequestMethod.POST})
    public Page<CoreExternalAuditGroupBean> selectAuditGroupPaginationByExternalAuditId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditGroupPaginationByExternalAuditId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5916\u90e8\u8bc4\u5ba1-\u5ba1\u6838\u7ec4", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/audit-groups"}, method={RequestMethod.POST})
    public Long insertAuditGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAuditGroup(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5916\u90e8\u8bc4\u5ba1-\u5ba1\u6838\u7ec4", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/audit-groups"}, method={RequestMethod.DELETE})
    public void deleteAuditGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAuditGroup(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5916\u90e8\u8bc4\u5ba1ID\u67e5\u8be2\u5916\u90e8\u8bc4\u5ba1-\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/audit-ncrs/queries"}, method={RequestMethod.POST})
    public Page<CoreExternalAuditNcrBean> selectAuditNcrPaginationByExternalAuditId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditNcrPaginationByExternalAuditId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5916\u90e8\u8bc4\u5ba1-\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/audit-ncrs"}, method={RequestMethod.POST})
    public Long insertAuditNcr(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAuditNcr(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5916\u90e8\u8bc4\u5ba1-\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/audit-ncrs"}, method={RequestMethod.DELETE})
    public void deleteAuditNcr(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAuditNcr(id, wrapper);
    }
}
