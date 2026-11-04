/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.service;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;
import java.util.Map;

public interface CoreCustomDsService
extends GenericService<CoreCustomDsBean, Long>,
GenericActivatableService<CoreCustomDsBean, Long>,
GenericChoosableService<CoreCustomDsBean, Long> {
    public void insertDsFields(Long var1, RestJsonWrapperBean var2);

    public List<CoreCustomDsDTO> getActiveCustomDsList();

    public List<Map<String, Object>> getCustomDsDataById(Long var1, RestJsonWrapperBean var2);

    public List<Map<String, Object>> previewSql(Long var1, RestJsonWrapperBean var2);

    public List<String> getPreviewColumnList(Long var1, RestJsonWrapperBean var2);

    public List<Object> getColumnValues(Long var1, RestJsonWrapperBean var2);
}
