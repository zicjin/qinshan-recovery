/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.resource.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImGroupBean;
import com.sunwayworld.cloud.module.im.bean.CoreImGroupContactsDTO;
import com.sunwayworld.cloud.module.im.resource.CoreImGroupResource;
import com.sunwayworld.cloud.module.im.service.CoreImGroupService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5373\u65f6\u804a\u5929\u597d\u53cb\u5206\u7ec4")
@RestController
@GikamBean
public class CoreImGroupResourceImpl
implements CoreImGroupResource,
AbstractGenericResource<CoreImGroupService, CoreImGroupBean, Long> {
    @Autowired
    private CoreImGroupService imGroupService;

    @Override
    public CoreImGroupService getService() {
        return this.imGroupService;
    }

    @Override
    public List<CoreImGroupContactsDTO> selectContactsList() {
        return this.getService().selectContactsList();
    }
}
