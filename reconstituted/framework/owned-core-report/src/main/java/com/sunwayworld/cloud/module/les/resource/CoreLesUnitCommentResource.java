/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitCommentBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreLesUnitCommentResource
extends GenericCloudResource<CoreLesUnitCommentBean, Long> {
    @RequestMapping(value={"/action/update"}, method={RequestMethod.PUT})
    public void update(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/reply"}, method={RequestMethod.PUT})
    public void reply(RestJsonWrapperBean var1);
}
