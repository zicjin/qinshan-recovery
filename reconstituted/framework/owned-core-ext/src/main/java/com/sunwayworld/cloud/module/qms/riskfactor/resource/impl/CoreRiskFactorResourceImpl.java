/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.resource.impl;

import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorBean;
import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorDTO;
import com.sunwayworld.cloud.module.qms.riskfactor.resource.CoreRiskFactorResource;
import com.sunwayworld.cloud.module.qms.riskfactor.service.CoreRiskFactorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u98ce\u9669\u56e0\u7d20")
@RestController
@GikamBean
public class CoreRiskFactorResourceImpl
implements CoreRiskFactorResource,
AbstractGenericResource<CoreRiskFactorService, CoreRiskFactorBean, Long>,
AbstractGenericSelectableResource<CoreRiskFactorService, CoreRiskFactorBean, Long> {
    @Autowired
    private CoreRiskFactorService riskFactorService;

    @Override
    public CoreRiskFactorService getService() {
        return this.riskFactorService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u98ce\u9669\u56e0\u7d20\u6811", type=LogType.SELECT)
    @RequestMapping(value={"/tree-grid"}, method={RequestMethod.POST})
    public Page<CoreRiskFactorDTO> selectTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectTreeList(wrapper);
    }
}
