/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.resource;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/im/messages"})
public interface CoreImMessageResource
extends GenericCloudResource<CoreImMessageBean, Long> {
    @RequestMapping(value={"/recent-contactses/queries"}, method={RequestMethod.POST})
    public Page<CoreImContactsDTO> selectRecentContactsPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/contactses/{contactsId}/queries"}, method={RequestMethod.POST})
    public Page<CoreImMessageBean> selectMessagePaginationByContactsId(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/contactses/{contactsId}/action/read"}, method={RequestMethod.PUT})
    public void read(@PathVariable String var1);

    @RequestMapping(value={"/action/insert"}, method={RequestMethod.POST})
    public Boolean insertMessage(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{userId}/action/top"}, method={RequestMethod.POST})
    public void topMessage(@PathVariable String var1);

    @RequestMapping(value={"/{userId}/action/delete-contacts"}, method={RequestMethod.POST})
    public void deleteContacts(@PathVariable String var1);
}
