/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomTableDTO;
import com.sunwayworld.cloud.module.item.customds.resource.CoreCustomTableResource;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomTableService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u62d6\u62fd\u7ec4\u4ef6\u83b7\u53d6\u6570\u636e\u5e93\u8868")
@RestController
@GikamBean
public class CoreCustomTableResourceImpl
implements CoreCustomTableResource {
    @Autowired
    private CoreCustomTableService customTableService;

    @Override
    @Log(value="\u901a\u7528\u67e5\u8be2", type=LogType.SELECT)
    public List<CoreCustomTableDTO> selectAllList(RestJsonWrapperBean wrapper) {
        return this.customTableService.selectAllList(wrapper);
    }

    @Override
    @Log(value="\u8868\u9884\u89c8\u6570\u636e", type=LogType.SELECT)
    public List<Map<String, Object>> previewSql(String tableName, RestJsonWrapperBean wrapper) {
        return this.customTableService.previewSql(tableName, wrapper);
    }
}
