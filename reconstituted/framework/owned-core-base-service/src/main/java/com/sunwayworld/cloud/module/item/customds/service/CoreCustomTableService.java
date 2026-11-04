/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.service;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomTableDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;
import java.util.Map;

public interface CoreCustomTableService {
    public List<CoreCustomTableDTO> selectAllList(RestJsonWrapperBean var1);

    public List<Map<String, Object>> previewSql(String var1, RestJsonWrapperBean var2);

    public void initDsTableData();
}
