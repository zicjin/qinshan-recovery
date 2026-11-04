/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.resource.impl;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaRegistBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.resource.CoreRiskIdentificaRegistResource;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaRegistService;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.validator.CoreRiskIdentificaRegistSubmitTaskValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u98ce\u9669\u8bc6\u522b\u767b\u8bb0")
@GikamBean
@RestController
public class CoreRiskIdentificaRegistResourceImpl
implements CoreRiskIdentificaRegistResource,
AbstractGenericResource<CoreRiskIdentificaRegistService, CoreRiskIdentificaRegistBean, Long>,
GenericAuditableResource<CoreRiskIdentificaRegistService, CoreRiskIdentificaRegistBean, Long>,
AbstractGenericChoosableResource<CoreRiskIdentificaRegistService, CoreRiskIdentificaRegistBean, Long> {
    @Autowired
    private CoreRiskIdentificaRegistService riskIdentificaRegistService;

    @Override
    public CoreRiskIdentificaRegistService getService() {
        return this.riskIdentificaRegistService;
    }

    @Override
    @Log(value="\u65b0\u589e\u98ce\u9669\u8bc6\u522b\u56e0\u7d20", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/risk-identifica-factors"}, method={RequestMethod.POST})
    public List<Long> insertRiskIdentificaFactor(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertRiskIdentificaFactor(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u98ce\u9669\u8bc6\u522b\u767b\u8bb0\u67e5\u8be2\u98ce\u9669\u8bc6\u522b\u56e0\u7d20", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/risk-identifica-factors/queries"}, method={RequestMethod.POST})
    public Page<CoreRiskIdentificaFactorBean> selectRiskIdentificaFactorPaginationByRegistId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectRiskIdentificaFactorPaginationByRegistId(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u98ce\u9669\u8bc6\u522b\u56e0\u7d20", type=LogType.DELETE)
    @RequestMapping(value={"/risk-identifica-factors"}, method={RequestMethod.DELETE})
    public void deleteRiskIdentificaFactor(RestJsonWrapperBean wrapper) {
        this.getService().deleteRiskIdentificaFactor(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreRiskIdentificaRegistSubmitTaskValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }
}
