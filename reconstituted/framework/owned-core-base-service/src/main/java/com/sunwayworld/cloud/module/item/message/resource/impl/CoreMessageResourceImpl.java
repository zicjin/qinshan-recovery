/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.resource.impl;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageBean;
import com.sunwayworld.cloud.module.item.message.bean.CoreMessageReceipentBean;
import com.sunwayworld.cloud.module.item.message.resource.CoreMessageResource;
import com.sunwayworld.cloud.module.item.message.service.CoreMessageReceipentService;
import com.sunwayworld.cloud.module.item.message.service.CoreMessageService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u7ad9\u5185\u6d88\u606f")
@GikamBean
public class CoreMessageResourceImpl
implements CoreMessageResource,
AbstractGenericResource<CoreMessageService, CoreMessageBean, Long>,
AbstractGenericActivatableResource<CoreMessageService, CoreMessageBean, Long> {
    @Autowired
    private CoreMessageService coreMessageService;
    @Autowired
    private CoreMessageReceipentService coreMessageReceipentService;

    @Override
    public CoreMessageService getService() {
        return this.coreMessageService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7ad9\u5185\u6d88\u606f", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u7ad9\u5185\u6d88\u606f\u63a5\u6536\u4eba", type=LogType.INSERT)
    public void insertMessageReceipent(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertMessageReceipent(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u7ad9\u5185\u6d88\u606f\u63a5\u6536\u4eba", type=LogType.DELETE)
    public void deleteMessageReceipent(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteMessageReceipent(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u7ad9\u5185\u6d88\u606f\u7684\u63a5\u6536\u4eba\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreMessageReceipentBean> selectReceipentPaginationByMessage(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreMessageReceipentService.selectPaginationByFilter(SearchFilter.instance().match("MESSAGEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u7b7e\u6536\u6d88\u606f", type=LogType.UPDATE)
    public void receive(RestJsonWrapperBean wrapper) {
        this.getService().receive(wrapper);
    }

    @Override
    public Pagination<CoreMessageReceipentBean> selectReceivablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreMessageReceipentService.selectPaginationByFilter(SearchFilter.instance().match("RECEIPENTID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ).match("RECEIVEDFLAG", "0").filter(MatchPattern.EQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void send(@RequestParam String subject, @RequestParam String content, @RequestParam(required=false) String receipents) {
        this.getService().send(subject, content, receipents);
    }
}
