/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import java.util.Map;

public interface CoreI18nMessageService
extends GenericService<CoreI18nMessageBean, Long> {
    public void initMessagesByI18nId(Long var1);

    public Map<String, Map<String, String>> selectMessageMapByLocaleGrouping();
}
