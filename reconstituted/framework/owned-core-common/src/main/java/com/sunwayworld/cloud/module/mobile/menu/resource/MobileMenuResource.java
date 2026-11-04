/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.resource;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuIcon;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface MobileMenuResource
extends GenericCloudResource<MobileMenuBean, Long>,
GenericActivatableCloudResource<MobileMenuBean, Long>,
GenericChoosableCloudResource<MobileMenuBean, Long> {
    @RequestMapping(value={"/icons/queries"}, method={RequestMethod.GET})
    public ArrayList<MobileMenuIcon> selectIconList();
}
