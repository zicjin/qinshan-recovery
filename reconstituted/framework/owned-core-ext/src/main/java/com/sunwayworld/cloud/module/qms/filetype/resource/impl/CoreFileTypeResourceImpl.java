/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.resource.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileTypeBean;
import com.sunwayworld.cloud.module.qms.filetype.resource.CoreFileTypeResource;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileTypeService;
import com.sunwayworld.cloud.module.qms.filetype.validator.CoreFileCategoryDeleteValidator;
import com.sunwayworld.cloud.module.qms.filetype.validator.CoreFileTypeDeleteValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import com.sunwayworld.framework.validator.permission.support.DefaultUpdatablePermissionValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6587\u4ef6\u7c7b\u578b")
@RestController
@GikamBean
public class CoreFileTypeResourceImpl
implements CoreFileTypeResource,
AbstractGenericResource<CoreFileTypeService, CoreFileTypeBean, Long>,
AbstractGenericSelectableResource<CoreFileTypeService, CoreFileTypeBean, Long>,
AbstractGenericActivatableResource<CoreFileTypeService, CoreFileTypeBean, Long> {
    @Autowired
    private CoreFileTypeService coreFileTypeService;

    @Override
    public CoreFileTypeService getService() {
        return this.coreFileTypeService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7c7b\u578b", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7c7b\u578b", type=LogType.DELETE)
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    @ValidateDataWith(value=CoreFileTypeDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7c7b\u578bID\u67e5\u8be2\u6587\u4ef6\u7c7b\u522b", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/categorys/queries"}, method={RequestMethod.POST})
    public Page<CoreFileCategoryBean> selectCategoryPaginationByFileTypeId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectCategoryPaginationByFileTypeId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7c7b\u522b", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/categorys"}, method={RequestMethod.POST})
    public Long insertCategory(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertCategory(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7c7b\u522b", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/categorys"}, method={RequestMethod.DELETE})
    @ValidateDataWith(value=CoreFileCategoryDeleteValidator.class)
    public void deleteCategory(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteCategory(id, wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\u6587\u4ef6\u7c7b\u522b", type=LogType.ACTIVATE)
    public void activateFileCategory(RestJsonWrapperBean wrapper) {
        this.getService().activateFileCategory(wrapper);
    }

    @Override
    @Log(value="\u505c\u7528\u6587\u4ef6\u7c7b\u522b", type=LogType.ACTIVATE)
    public void deactivateFileCategory(RestJsonWrapperBean wrapper) {
        this.getService().deactivateFileCategory(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7c7b\u578bID\u67e5\u8be2\u6587\u4ef6\u7c7b\u522b\u89d2\u8272\u6388\u6743", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/category-role-auths/queries"}, method={RequestMethod.POST})
    public Page<CoreFileCategoryRoleAuthBean> selectCategoryRoleAuthPaginationByFileTypeId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectCategoryRoleAuthPaginationByFileTypeId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7c7b\u522b\u89d2\u8272\u6388\u6743", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/category-role-auths"}, method={RequestMethod.POST})
    public Long insertCategoryRoleAuth(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertCategoryRoleAuth(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7c7b\u522b\u89d2\u8272\u6388\u6743", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/category-role-auths"}, method={RequestMethod.DELETE})
    public void deleteCategoryRoleAuth(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteCategoryRoleAuth(id, wrapper);
    }

    @Override
    @Log(value="\u590d\u5236\u6587\u4ef6\u7c7b\u522b\u8d77\u8349\u6a21\u677f", type=LogType.UPDATE)
    @RequestMapping(value={"/file-categorys/{id}/action/copyfile"}, method={RequestMethod.PUT})
    public List<CoreFileCategoryBean> copyCategoryDraftTemplate(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().copyCategoryDraftTemplate(id, wrapper);
    }
}
