/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateOrgBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreLesTemplateOrgService
extends GenericService<CoreLesTemplateOrgBean, Long> {
    public void insertTemplateOrg(RestJsonWrapperBean var1);
}
