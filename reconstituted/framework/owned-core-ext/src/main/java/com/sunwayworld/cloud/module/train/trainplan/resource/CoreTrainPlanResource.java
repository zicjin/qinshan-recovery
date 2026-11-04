/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.resource;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanUserBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/train-plans"})
public interface CoreTrainPlanResource
extends GenericCloudResource<CoreTrainPlanBean, Long>,
GenericAuditableCloudResource<CoreTrainPlanBean, Long>,
GenericChoosableCloudResource<CoreTrainPlanBean, Long> {
    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.POST})
    public void insertUser(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/users"}, method={RequestMethod.DELETE})
    public void deleteUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/users/queries"}, method={RequestMethod.POST})
    public Pagination<CoreTrainPlanUserBean> selectTrainPlanUserPaginationByPlanId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
