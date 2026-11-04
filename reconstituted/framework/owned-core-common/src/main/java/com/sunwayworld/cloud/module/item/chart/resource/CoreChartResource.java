/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.resource;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartLinesRepresentation;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/core/module/item/charts"})
public interface CoreChartResource
extends GenericCloudResource<CoreChartBean, String>,
GenericSelectableCloudResource<CoreChartBean, String> {
    @RequestMapping(value={"/{id}/configs"}, method={RequestMethod.POST})
    public Long insertConfig(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/configs"}, method={RequestMethod.PUT})
    public void saveConfig(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/configs"}, method={RequestMethod.DELETE})
    public void deleteConfig(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/search-configs"}, method={RequestMethod.POST})
    public Long insertSearchConfig(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/search-configs"}, method={RequestMethod.DELETE})
    public void deleteSearchConfig(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/submit"}, method={RequestMethod.PUT})
    public void submit(@PathVariable(value="id") String var1);

    @RequestMapping(value={"/{id}/sql/queries"}, method={RequestMethod.POST})
    public Pagination<Map<String, Object>> selectMapPaginationByChartSql(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/search-configs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreChartSearchConfigBean> selectSearchConfigPaginationByChart(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/configs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreChartConfigBean> selectConfigPaginationByChart(@PathVariable(value="id") String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/package-lines"}, method={RequestMethod.POST})
    public CoreChartLinesRepresentation packageLinesByConfig(@PathVariable(value="id") String var1);
}
