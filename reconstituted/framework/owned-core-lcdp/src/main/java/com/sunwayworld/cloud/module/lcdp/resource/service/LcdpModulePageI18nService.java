/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface LcdpModulePageI18nService
extends GenericService<LcdpModulePageI18nBean, Long> {
    public Map<String, String> selectI18nMessageByCode(RestJsonWrapperBean var1);

    public Map<String, Map<String, String>> selectPageI18nMessage(Long var1);

    public Page<Map<String, Object>> selectAllI18nMessage(RestJsonWrapperBean var1);

    public List<LcdpModulePageI18nBean> selectEffectivePageI18nMessage(MapperParameter var1);

    public void alterMessage(RestJsonWrapperBean var1);

    public String export(RestJsonWrapperBean var1);

    public void batchDealHistoryPageCompI18n(List<LcdpResourceHistoryBean> var1, Map<String, String> var2);

    public void batchDealHistoryPageCompI18nForDuplicateId(List<LcdpResourceHistoryBean> var1);
}
