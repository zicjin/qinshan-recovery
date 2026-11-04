/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.resource;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaRegistBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/risk-identifica-regists"})
public interface CoreRiskIdentificaRegistResource
extends GenericCloudResource<CoreRiskIdentificaRegistBean, Long> {
    public List<Long> insertRiskIdentificaFactor(Long var1, RestJsonWrapperBean var2);

    public void deleteRiskIdentificaFactor(RestJsonWrapperBean var1);

    public Page<CoreRiskIdentificaFactorBean> selectRiskIdentificaFactorPaginationByRegistId(Long var1, RestJsonWrapperBean var2);
}
