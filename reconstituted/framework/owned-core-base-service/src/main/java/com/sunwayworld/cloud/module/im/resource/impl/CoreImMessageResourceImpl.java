/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.resource.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.cloud.module.im.resource.CoreImMessageResource;
import com.sunwayworld.cloud.module.im.service.CoreImMessageService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5373\u65f6\u804a\u5929\u6d88\u606f")
@RestController
@GikamBean
public class CoreImMessageResourceImpl
implements CoreImMessageResource,
AbstractGenericResource<CoreImMessageService, CoreImMessageBean, Long> {
    @Autowired
    private CoreImMessageService imMessageService;

    @Override
    public CoreImMessageService getService() {
        return this.imMessageService;
    }

    @Override
    public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectRecentContactsPagination(wrapper);
    }

    @Override
    public Page<CoreImMessageBean> selectMessagePaginationByContactsId(String contactsId, RestJsonWrapperBean wrapper) {
        return this.getService().selectMessagePaginationByContactsId(contactsId, wrapper);
    }

    @Override
    public Boolean insertMessage(RestJsonWrapperBean wrapper) {
        return this.getService().insertMessage(wrapper);
    }

    @Override
    public void topMessage(String userId) {
        this.getService().topMessage(userId);
    }

    @Override
    public void deleteContacts(String userId) {
        this.getService().deleteContacts(userId);
    }

    @Override
    public void read(String contactsId) {
        this.getService().read(contactsId);
    }
}
