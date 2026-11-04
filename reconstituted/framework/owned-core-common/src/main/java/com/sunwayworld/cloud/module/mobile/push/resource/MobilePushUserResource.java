/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.resource;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface MobilePushUserResource
extends GenericCloudResource<MobilePushUserBean, String>,
GenericChoosableCloudResource<MobilePushUserBean, String> {
    @RequestMapping(value={"/insert"}, method={RequestMethod.POST})
    public void insertPushUser(@RequestParam String var1, @RequestParam String var2);
}
