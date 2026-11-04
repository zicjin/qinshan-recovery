/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.resource;

import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorBean;
import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/risk-factors"})
public interface CoreRiskFactorResource
extends GenericCloudResource<CoreRiskFactorBean, Long> {
    public Page<CoreRiskFactorDTO> selectTree(RestJsonWrapperBean var1);
}
