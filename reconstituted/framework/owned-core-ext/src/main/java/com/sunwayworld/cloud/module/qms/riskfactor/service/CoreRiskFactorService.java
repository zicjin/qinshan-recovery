/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.service;

import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorBean;
import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreRiskFactorService
extends GenericService<CoreRiskFactorBean, Long>,
GenericSelectableService<CoreRiskFactorBean, Long> {
    public Page<CoreRiskFactorDTO> selectTreeList(RestJsonWrapperBean var1);
}
