/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.resource.impl;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.resource.CoreQuestionResource;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionOptService;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionService;
import com.sunwayworld.cloud.module.train.question.validator.CoreQuestionActivatableValidator;
import com.sunwayworld.cloud.module.train.question.validator.CoreQuestionDeactivatableValidator;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.suspendable.resource.AbstractGenericSuspendableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9898\u5e93\u7ba1\u7406")
@RestController
@GikamBean
public class CoreQuestionResourceImpl
implements CoreQuestionResource,
AbstractGenericResource<CoreQuestionService, CoreQuestionBean, Long>,
AbstractGenericActivatableResource<CoreQuestionService, CoreQuestionBean, Long>,
AbstractGenericSuspendableResource<CoreQuestionService, CoreQuestionBean, Long> {
    @Autowired
    private CoreQuestionService coreQuestionService;
    @Autowired
    private CoreQuestionOptService coreQuestionOptService;

    @Override
    public CoreQuestionService getService() {
        return this.coreQuestionService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9898\u76ee\u9009\u9879", type=LogType.INSERT)
    public void insertOpt(Long id, RestJsonWrapperBean wrapper) {
        this.coreQuestionOptService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u9898\u76ee\u9009\u9879", type=LogType.DELETE)
    public void deleteOpt(RestJsonWrapperBean wrapper) {
        this.coreQuestionOptService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u9898\u76ee\u67e5\u8be2\u9898\u76ee\u9009\u9879", type=LogType.SELECT)
    public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByQuestionId(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreQuestionOptService.selectPaginationByFilter(SearchFilter.instance().match("QUESTIONID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u590d\u5236\u9898\u76ee", type=LogType.INSERT)
    public ArrayList<Long> clone(RestJsonWrapperBean wrapper) {
        return (ArrayList)this.coreQuestionService.clone(wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\u9898\u76ee", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreQuestionActivatableValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.coreQuestionService.activate(wrapper);
    }

    @Override
    @Log(value="\u505c\u7528\u9898\u76ee", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreQuestionDeactivatableValidator.class)
    public void deactivate(RestJsonWrapperBean wrapper) {
        this.coreQuestionService.deactivate(wrapper);
    }
}
