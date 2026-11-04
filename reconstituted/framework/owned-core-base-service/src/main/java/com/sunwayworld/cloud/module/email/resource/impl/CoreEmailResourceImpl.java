/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.resource.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.cloud.module.email.resource.CoreEmailResource;
import com.sunwayworld.cloud.module.email.service.CoreEmailReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u90ae\u4ef6\u53d1\u9001")
@GikamBean
@RequestMapping(value={"/secure/core/module/item/emails"})
public class CoreEmailResourceImpl
implements CoreEmailResource,
AbstractGenericResource<CoreEmailService, CoreEmailBean, Long> {
    @Autowired
    private CoreEmailService emailService;
    @Autowired
    private CoreEmailReceipentService receipentService;

    @Override
    public CoreEmailService getService() {
        return this.emailService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u90ae\u4ef6\u53d1\u9001\u7684\u63a5\u6536\u4eba\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreEmailReceipentBean> selectReceiptentByEmail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        SearchFilter filter = SearchFilter.instance().match("EMAILID", id).filter(MatchPattern.EQ);
        return (Pagination)this.receipentService.selectPaginationByFilter(filter, wrapper);
    }
}
