/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.service;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsBean;
import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreImContactsService
extends GenericService<CoreImContactsBean, Long> {
    public void insertByMessage(CoreImMessageBean var1);

    public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean var1);

    public void topMessage(String var1);

    public void deleteContacts(String var1);
}
