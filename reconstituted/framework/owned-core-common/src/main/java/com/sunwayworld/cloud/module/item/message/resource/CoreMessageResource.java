/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.resource;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageBean;
import com.sunwayworld.cloud.module.item.message.bean.CoreMessageReceipentBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/item/messages"})
public interface CoreMessageResource
extends GenericCloudResource<CoreMessageBean, Long>,
GenericActivatableCloudResource<CoreMessageBean, Long> {
    @RequestMapping(value={"/{id}/receipents"}, method={RequestMethod.POST})
    public void insertMessageReceipent(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/receipents"}, method={RequestMethod.DELETE})
    public void deleteMessageReceipent(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/receipents/queries"}, method={RequestMethod.POST})
    public Pagination<CoreMessageReceipentBean> selectReceipentPaginationByMessage(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/receive"}, method={RequestMethod.PUT})
    public void receive(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/receivable"}, method={RequestMethod.POST})
    public Pagination<CoreMessageReceipentBean> selectReceivablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/send"}, method={RequestMethod.PUT})
    public void send(@RequestParam String var1, @RequestParam String var2, @RequestParam(required=false) String var3);
}
