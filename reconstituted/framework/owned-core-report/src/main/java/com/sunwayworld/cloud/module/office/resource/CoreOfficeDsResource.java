/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;

public interface CoreOfficeDsResource
extends GenericCloudResource<CoreOfficeDsBean, Long>,
GenericActivatableCloudResource<CoreOfficeDsBean, Long>,
GenericChoosableCloudResource<CoreOfficeDsBean, Long> {
    public Long insertField(Long var1, RestJsonWrapperBean var2);

    public void deleteField(Long var1, RestJsonWrapperBean var2);

    public Pagination<CoreOfficeDsFieldBean> selectFieldPaginationByDsId(Long var1, RestJsonWrapperBean var2);

    public RestValidationResultBean updateSqlIfValid(Long var1, RestJsonWrapperBean var2);

    public void swapField(Long var1, Long var2);

    public String selectTree();
}
