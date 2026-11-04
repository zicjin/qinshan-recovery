/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.service;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreImMessageService
extends GenericService<CoreImMessageBean, Long> {
    public void insert(CoreImMessageBean var1);

    public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean var1);

    public Page<CoreImMessageBean> selectMessagePaginationByContactsId(String var1, RestJsonWrapperBean var2);

    public Boolean insertMessage(RestJsonWrapperBean var1);

    public void read(String var1);

    public void topMessage(String var1);

    public void deleteContacts(String var1);
}
