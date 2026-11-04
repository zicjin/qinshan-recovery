/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.resource;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushMessageBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface MobilePushMessageResource
extends GenericCloudResource<MobilePushMessageBean, Long>,
GenericChoosableCloudResource<MobilePushMessageBean, Long> {
    @RequestMapping(value={"/action/send"}, method={RequestMethod.PUT})
    public void send(RestJsonWrapperBean var1);
}
