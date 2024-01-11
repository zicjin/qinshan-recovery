package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.resource.EmcEquiptCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@LogModule(value = "仪器类型")
@RestController
@GikamBean
public class EmcEquiptCategoryResourceImpl implements EmcEquiptCategoryResource {
    @Autowired
    private EmcEquiptCategoryService equiptCategoryService;

    @Override
    public EmcEquiptCategoryService getService() {
        return equiptCategoryService;
    }

    @Override
    @Log(value = "新增仪器类型", type = LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询仪器维护事件", type = LogType.SELECT)
    public Page<EmcEquiptMaintBean> selectEquiptMaintPaginationByEquiptCategoryId(Long id, RestJsonWrapperBean wrapper) {
        return getService().selectEquiptMaintPaginationByEquiptCategoryId(id, wrapper);
    }

    @Override
    @Log(value = "新增仪器维护事件", type = LogType.INSERT)
    public Long insertEquiptMaint(Long id, RestJsonWrapperBean wrapper) {
        return getService().insertEquiptMaint(id, wrapper);
    }

    @Override
    @Log(value = "删除仪器维护事件", type = LogType.DELETE)
    public void deleteEquiptMaint(Long id, RestJsonWrapperBean wrapper) {
        getService().deleteEquiptMaint(id, wrapper);
    }

}
