/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.resource.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.cloud.module.lcdp.bp.resource.LcdpBizProcessManagerResource;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5de5\u4f5c\u6d41\u5904\u7406")
@RestController
@GikamBean
public class LcdpBizProcessManagerResourceImpl
implements LcdpBizProcessManagerResource,
AbstractGenericResource<LcdpBizProcessService, LcdpBizProcessBean, Long> {
    @Autowired
    private LcdpBizProcessService lcdpBizProcessService;

    @Override
    public LcdpBizProcessService getService() {
        return this.lcdpBizProcessService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c", type=LogType.SELECT)
    public List<LcdpBizProcessNodeActionBean> selectBpNodeActionList(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeActionList(wrapper);
    }

    @Override
    @Log(value="\u4e1a\u52a1\u5de5\u4f5c\u6d41\u5ba1\u6279", type=LogType.UPDATE)
    public void approval(RestJsonWrapperBean wrapper) {
        this.getService().approval(wrapper);
    }
}
