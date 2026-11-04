/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.code.validator.CoreCodeInsertableValidator;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.resource.CoreI18nResource;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u7cfb\u7edf\u56fd\u9645\u5316")
public class CoreI18nResourceImpl
implements CoreI18nResource {
    @Autowired
    private CoreI18nService service;
    @Autowired
    private CoreI18nMessageService messageService;

    @Override
    public CoreI18nService getService() {
        return this.service;
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    @ResponseStatus(value=HttpStatus.OK)
    @ValidateDataWith(value=CoreCodeInsertableValidator.class)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        return (Long)this.getService().insert(jsonWrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u56fd\u9645\u5316\u7684ID\u67e5\u8be2\u56fd\u9645\u5316\u4fe1\u606f", type=LogType.SELECT)
    public Page<CoreI18nMessageBean> selectMessagePaginationByI18nId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.messageService.selectPaginationByFilter(SearchFilter.instance().match("I18NID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u56fd\u9645\u5316\u7684ID\u521d\u59cb\u5316\u56fd\u9645\u5316\u4fe1\u606f", type=LogType.UPDATE)
    public void initMessagesByI18nId(@PathVariable Long id) {
        this.messageService.initMessagesByI18nId(id);
    }
}
