/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.resource.impl;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanUserBean;
import com.sunwayworld.cloud.module.train.trainplan.resource.CoreTrainPlanResource;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanService;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanUserService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.AbstractGenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u57f9\u8bad\u8ba1\u5212")
@RestController
@GikamBean
public class CoreTrainPlanResourceImpl
implements CoreTrainPlanResource,
AbstractGenericResource<CoreTrainPlanService, CoreTrainPlanBean, Long>,
AbstractGenericAuditableResource<CoreTrainPlanService, CoreTrainPlanBean, Long>,
AbstractGenericChoosableResource<CoreTrainPlanService, CoreTrainPlanBean, Long> {
    @Autowired
    private CoreTrainPlanService coreTrainPlanService;
    @Autowired
    private CoreTrainPlanUserService coreTrainPlanUserService;

    @Override
    public CoreTrainPlanService getService() {
        return this.coreTrainPlanService;
    }

    @Override
    @Log(value="\u65b0\u589e\u57f9\u8bad\u8ba1\u5212\u53c2\u4e0e\u4eba\u5458", type=LogType.INSERT)
    public void insertUser(Long id, RestJsonWrapperBean wrapper) {
        this.coreTrainPlanUserService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u57f9\u8bad\u8ba1\u5212\u53c2\u4e0e\u4eba\u5458", type=LogType.DELETE)
    public void deleteUser(RestJsonWrapperBean wrapper) {
        this.coreTrainPlanUserService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u57f9\u8bad\u8ba1\u5212\u67e5\u8be2\u57f9\u8bad\u8ba1\u5212\u53c2\u4e0e\u4eba\u5458", type=LogType.SELECT)
    public Pagination<CoreTrainPlanUserBean> selectTrainPlanUserPaginationByPlanId(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreTrainPlanUserService.selectPaginationByFilter(SearchFilter.instance().match("TRAINPLANID", id).filter(MatchPattern.EQ), wrapper);
    }
}
