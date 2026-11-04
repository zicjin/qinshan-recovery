/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.service;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreCustomDsSqlService
extends GenericService<CoreCustomDsSqlBean, Long> {
    public RestValidationResultBean validateSql(RestJsonWrapperBean var1);

    public List<CoreCustomDsSqlDTO> selectAllList(RestJsonWrapperBean var1);

    public List<Map<String, Object>> previewSql(Long var1, RestJsonWrapperBean var2);
}
