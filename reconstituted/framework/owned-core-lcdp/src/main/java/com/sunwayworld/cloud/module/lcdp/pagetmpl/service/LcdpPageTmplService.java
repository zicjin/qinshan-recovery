/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.service;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpPageTmplService
extends GenericService<LcdpPageTmplBean, Long> {
    public Long insertByPageTmpl(RestJsonWrapperBean var1);
}
