/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.service;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaRegistBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreRiskIdentificaRegistService
extends GenericService<CoreRiskIdentificaRegistBean, Long>,
GenericAuditableService<CoreRiskIdentificaRegistBean, Long>,
GenericChoosableService<CoreRiskIdentificaRegistBean, Long> {
    public List<Long> insertRiskIdentificaFactor(Long var1, RestJsonWrapperBean var2);

    public Page<CoreRiskIdentificaFactorBean> selectRiskIdentificaFactorPaginationByRegistId(Long var1, RestJsonWrapperBean var2);

    public void deleteRiskIdentificaFactor(RestJsonWrapperBean var1);
}
