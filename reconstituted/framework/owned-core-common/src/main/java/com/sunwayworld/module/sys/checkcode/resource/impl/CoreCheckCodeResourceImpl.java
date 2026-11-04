/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.checkcode.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import com.sunwayworld.module.sys.checkcode.resource.CoreCheckCodeResource;
import com.sunwayworld.module.sys.checkcode.service.CoreCheckCodeService;
import com.sunwayworld.module.sys.checkcode.validator.CoreCheckCodeInsertableValidator;
import com.sunwayworld.module.sys.checkcode.validator.CoreCheckCodeInstantSaveValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u590d\u9009\u6846\u7f16\u7801")
@RestController
@GikamBean
public class CoreCheckCodeResourceImpl
implements CoreCheckCodeResource {
    @Autowired
    private CoreCheckCodeService coreCheckCodeService;

    @Override
    public CoreCheckCodeService getService() {
        return this.coreCheckCodeService;
    }

    @Override
    @Log(value="\u65b0\u589e\u590d\u9009\u6846\u7f16\u7801", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    @ValidateDataWith(value=CoreCheckCodeInsertableValidator.class)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u590d\u9009\u6846\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/checkable"}, method={RequestMethod.GET})
    public List<CoreCheckCodeBean> selectCheckableList() {
        return this.getService().selectCheckableList();
    }

    @Override
    @Log(value="\u590d\u9009\u6846\u6570\u636e\u5b9e\u65f6\u4fee\u6539", type=LogType.UPDATE)
    @ValidateDataWith(value=CoreCheckCodeInstantSaveValidator.class)
    public void instantSave(RestJsonWrapperBean wrapper) {
        this.getService().instantSave(wrapper);
    }

    @Override
    @Log(value="\u590d\u9009\u6846\u7f16\u7801\u5206\u7c7b\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/categories/queries"}, method={RequestMethod.GET})
    public List<String> selectCategoryList() {
        return this.getService().selectCategoryList();
    }
}
