/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.resource.impl;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionBean;
import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionItemBean;
import com.sunwayworld.cloud.module.qms.prevention.resource.CorePreventionResource;
import com.sunwayworld.cloud.module.qms.prevention.service.CorePreventionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9884\u9632\u63aa\u65bd")
@RestController
@GikamBean
public class CorePreventionResourceImpl
implements CorePreventionResource,
AbstractGenericResource<CorePreventionService, CorePreventionBean, Long>,
GenericAuditableResource<CorePreventionService, CorePreventionBean, Long> {
    @Autowired
    private CorePreventionService corePreventionService;

    @Override
    public CorePreventionService getService() {
        return this.corePreventionService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9884\u9632\u63aa\u65bd", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u9884\u9632\u63aa\u65bdID\u67e5\u8be2\u9884\u9632\u63aa\u65bd-\u5b50\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/items/queries"}, method={RequestMethod.POST})
    public Page<CorePreventionItemBean> selectItemPaginationByPreventionId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectItemPaginationByPreventionId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u9884\u9632\u63aa\u65bd-\u5b50\u8868", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/items"}, method={RequestMethod.POST})
    public Long insertItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u9884\u9632\u63aa\u65bd-\u5b50\u8868", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/items"}, method={RequestMethod.DELETE})
    public void deleteItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteItem(id, wrapper);
    }
}
