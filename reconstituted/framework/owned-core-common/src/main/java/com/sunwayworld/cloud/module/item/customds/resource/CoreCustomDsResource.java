/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/custom-dses"})
public interface CoreCustomDsResource
extends GenericCloudResource<CoreCustomDsBean, Long>,
GenericActivatableCloudResource<CoreCustomDsBean, Long>,
GenericChoosableCloudResource<CoreCustomDsBean, Long> {
    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomDsFieldBean> selectDsFieldPaginationByCustomDsId(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapField(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertDsField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteDsField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/active-custom-ds-list"}, method={RequestMethod.POST})
    public List<CoreCustomDsDTO> getActiveCustomDsList();

    @RequestMapping(value={"/{dsId}/sql-result-datas"}, method={RequestMethod.POST})
    public List<Map<String, Object>> selectDsResultData(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{dsId}/action/insert-ds-fields"}, method={RequestMethod.POST})
    public void insertDsFields(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/preview-columns/queries"}, method={RequestMethod.POST})
    public List<String> getPreviewColumnList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/preview"}, method={RequestMethod.POST})
    public List<Map<String, Object>> previewSql(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{dsId}/column-values"}, method={RequestMethod.POST})
    public List<Object> getColumnValues(@PathVariable Long var1, RestJsonWrapperBean var2);
}
