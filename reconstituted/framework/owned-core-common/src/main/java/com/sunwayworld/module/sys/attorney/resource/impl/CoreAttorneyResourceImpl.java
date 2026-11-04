/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyDTO;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import com.sunwayworld.module.sys.attorney.resource.CoreAttorneyResource;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyService;
import com.sunwayworld.module.sys.attorney.validator.CoreAttorneyActivateDataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u59d4\u6258\u4ee3\u7406")
@RestController
@GikamBean
public class CoreAttorneyResourceImpl
implements CoreAttorneyResource {
    @Autowired
    private CoreAttorneyService attorneyService;

    @Override
    public CoreAttorneyService getService() {
        return this.attorneyService;
    }

    @Override
    public Page<CoreAttorneyMenuBean> selectAttorneyMenuPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAttorneyMenuPagination(id, wrapper);
    }

    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    public void insertAttorneyMenus(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertAttorneyMenus(id, wrapper);
    }

    @Override
    public void deleteAttorneyMenus(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAttorneyMenus(id, wrapper);
    }

    @Override
    @ValidateDataWith(value=CoreAttorneyActivateDataValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        CoreAttorneyResource.super.activate(wrapper);
    }

    @Override
    public void switchTrustor(@PathVariable String trustorId) {
        this.getService().switchTrustor(trustorId);
    }

    @Override
    public void switchTrustee() {
        this.getService().switchTrustee();
    }

    @Override
    public List<CoreAttorneyDTO> selectAttorneyTodoList() {
        return this.getService().selectAttorneyTodoList();
    }
}
