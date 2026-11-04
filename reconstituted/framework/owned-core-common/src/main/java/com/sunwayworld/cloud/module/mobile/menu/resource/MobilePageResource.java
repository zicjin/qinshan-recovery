/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.resource;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageRepresentative;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface MobilePageResource
extends GenericCloudResource<MobilePageBean, Long>,
GenericChoosableCloudResource<MobilePageBean, Long> {
    @RequestMapping(value={"pages/{id}"}, method={RequestMethod.GET})
    public MobilePageBean selectPage(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/mobile-page-same-level"}, method={RequestMethod.POST})
    public void insertSameLevelPage(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/mobile-page-lower-level"}, method={RequestMethod.POST})
    public void insertLowerLevelPage(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/mobile-page"}, method={RequestMethod.DELETE})
    public void deletePage(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/mobile-page/tree-grids"}, method={RequestMethod.POST})
    public Pagination<MobilePageRepresentative> selectMobilePageTreeList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/switch/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void switchPageColumn(@PathVariable Long var1, @PathVariable Long var2);
}
