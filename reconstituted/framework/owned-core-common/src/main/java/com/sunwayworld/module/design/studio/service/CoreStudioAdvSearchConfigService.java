/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import java.util.List;

public interface CoreStudioAdvSearchConfigService
extends GenericService<CoreStudioAdvSearchConfigBean, Long> {
    public Long save(RestJsonWrapperBean var1);

    public List<CoreStudioAdvSearchConfigBean> getCurrentUserConfigListByUrl(String var1);
}
