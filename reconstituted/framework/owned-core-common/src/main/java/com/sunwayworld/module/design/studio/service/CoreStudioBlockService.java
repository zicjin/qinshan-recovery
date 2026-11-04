/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockConfigDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockLineBean;

public interface CoreStudioBlockService
extends GenericService<CoreStudioBlockBean, Long> {
    public Page<CoreStudioBlockBean> selectPaginationByPageUrl(RestJsonWrapperBean var1);

    public Page<CoreStudioBlockLineBean> selectLinePaginationByMasterId(Long var1, RestJsonWrapperBean var2);

    public Long save(RestJsonWrapperBean var1);

    public CoreStudioBlockConfigDTO getStudioBlockConfigByPageUrl(String var1);
}
