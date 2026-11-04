/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.resource;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/bp-managers"})
public interface LcdpBizProcessManagerResource
extends GenericCloudResource<LcdpBizProcessBean, Long> {
    @RequestMapping(value={"/bp-node-actions/queries"}, method={RequestMethod.POST})
    public List<LcdpBizProcessNodeActionBean> selectBpNodeActionList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/approval"}, method={RequestMethod.PUT})
    public void approval(RestJsonWrapperBean var1);
}
