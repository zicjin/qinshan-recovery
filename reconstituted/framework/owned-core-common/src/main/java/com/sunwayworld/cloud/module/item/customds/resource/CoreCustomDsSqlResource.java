/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value={"/secure/core/module/item/custom-sqls"})
public interface CoreCustomDsSqlResource {
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.DELETE})
    public void delete(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    public void save(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public List<CoreCustomDsSqlDTO> selectAllList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/validate-sql"}, method={RequestMethod.POST})
    public RestValidationResultBean validateSql(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/preview"}, method={RequestMethod.POST})
    public List<Map<String, Object>> previewSql(@PathVariable Long var1, RestJsonWrapperBean var2);
}
