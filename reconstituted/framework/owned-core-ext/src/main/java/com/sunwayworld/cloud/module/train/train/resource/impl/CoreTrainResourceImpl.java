/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.resource.impl;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.cloud.module.train.train.resource.CoreTrainResource;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainService;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainUserService;
import com.sunwayworld.cloud.module.train.train.validator.CoreTrainActivatableValidator;
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
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u57f9\u8bad\u5b9e\u65bd")
@RestController
@GikamBean
public class CoreTrainResourceImpl
implements CoreTrainResource,
AbstractGenericResource<CoreTrainService, CoreTrainBean, Long>,
AbstractGenericActivatableResource<CoreTrainService, CoreTrainBean, Long> {
    @Autowired
    private CoreTrainService coreTrainService;
    @Autowired
    private CoreTrainUserService coreTrainUserService;

    @Override
    public CoreTrainService getService() {
        return this.coreTrainService;
    }

    @Override
    @Log(value="\u65b0\u589e\u57f9\u8bad\u5b9e\u65bd\u53c2\u4e0e\u4eba\u5458", type=LogType.INSERT)
    public void insertUser(Long id, RestJsonWrapperBean wrapper) {
        this.coreTrainUserService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u57f9\u8bad\u5b9e\u65bd\u53c2\u4e0e\u4eba\u5458", type=LogType.DELETE)
    public void deleteUser(RestJsonWrapperBean wrapper) {
        this.coreTrainUserService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u57f9\u8bad\u5b9e\u65bd\u67e5\u8be2\u57f9\u8bad\u8ba1\u5212\u53c2\u4e0e\u4eba\u5458", type=LogType.SELECT)
    public Pagination<CoreTrainUserBean> selectTrainUserPaginationByTrainId(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreTrainUserService.selectPaginationByFilter(SearchFilter.instance().match("TRAINID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u7528\u6237\u7b7e\u5230", type=LogType.UPDATE)
    public void userSign(Long id, RestJsonWrapperBean wrapper) {
        this.coreTrainUserService.sign(id, wrapper);
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u542f\u7528", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreTrainActivatableValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.coreTrainService.activate(wrapper);
    }
}
