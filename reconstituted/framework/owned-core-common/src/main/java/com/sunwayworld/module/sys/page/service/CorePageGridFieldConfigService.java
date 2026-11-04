/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.design.studio.bean.CoreStudioGridDTO;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import java.util.List;

public interface CorePageGridFieldConfigService
extends GenericService<CorePageGridFieldConfigBean, Long> {
    public void insertOrUpdateCurrentGridConfig(RestJsonWrapperBean var1);

    public List<CorePageGridFieldConfigBean> selectCurrentGridConfigList(String var1, String var2);

    public void resetPageGridConfig(List<CoreStudioGridDTO> var1, List<CoreStudioGridDTO> var2);
}
