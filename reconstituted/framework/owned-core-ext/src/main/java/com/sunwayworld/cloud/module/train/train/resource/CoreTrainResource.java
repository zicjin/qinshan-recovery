/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.resource;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/trains"})
public interface CoreTrainResource
extends GenericCloudResource<CoreTrainBean, Long>,
GenericActivatableCloudResource<CoreTrainBean, Long> {
    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.POST})
    public void insertUser(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/users"}, method={RequestMethod.DELETE})
    public void deleteUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/action/user-sign"}, method={RequestMethod.PUT})
    public void userSign(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/users/queries"}, method={RequestMethod.POST})
    public Pagination<CoreTrainUserBean> selectTrainUserPaginationByTrainId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
