/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlog.resource;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/submit-logs"})
public interface LcdpSubmitLogResource
extends GenericCloudResource<LcdpSubmitLogBean, Long> {
    @RequestMapping(value={"/resources/action/submit"}, method={RequestMethod.POST})
    public void submitResource(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/versions/queries"}, method={RequestMethod.POST})
    public Page<LcdpResourceVersionBean> selectVersionPaginationByLogId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/resources/action/view"}, method={RequestMethod.POST})
    public Page<LcdpResourceVersionBean> viewResource(RestJsonWrapperBean var1);
}
