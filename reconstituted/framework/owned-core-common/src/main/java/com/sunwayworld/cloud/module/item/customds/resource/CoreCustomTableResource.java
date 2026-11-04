/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomTableDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/custom-tables"})
public interface CoreCustomTableResource {
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public List<CoreCustomTableDTO> selectAllList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{tableName}/preview"}, method={RequestMethod.POST})
    public List<Map<String, Object>> previewSql(@PathVariable String var1, RestJsonWrapperBean var2);
}
