/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.resource;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreElnDsResource
extends GenericCloudResource<CoreElnDsBean, Long>,
GenericActivatableCloudResource<CoreElnDsBean, Long>,
GenericChoosableCloudResource<CoreElnDsBean, Long> {
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CoreElnDsFieldBean> selectFieldPaginationByDs(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/update-sql-if-valid"}, method={RequestMethod.PUT})
    public RestValidationResultBean updateSqlIfValid(@PathVariable Long var1, RestJsonWrapperBean var2);
}
