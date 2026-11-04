/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesDsFieldBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreLesDsResource
extends GenericCloudResource<CoreLesDsBean, Long>,
GenericActivatableCloudResource<CoreLesDsBean, Long>,
GenericChoosableCloudResource<CoreLesDsBean, Long> {
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CoreLesDsFieldBean> selectFieldPaginationByDs(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/update-sql-if-valid"}, method={RequestMethod.PUT})
    public RestValidationResultBean updateSqlIfValid(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapField(@PathVariable Long var1, @PathVariable Long var2);
}
