/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateOrgBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitDTO;
import com.sunwayworld.cloud.module.les.resource.CoreLesTemplateResource;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateOrgService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateService;
import com.sunwayworld.cloud.module.les.validator.CoreLesDeleteValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.AbstractGenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="LES\u6a21\u677f")
@GikamBean
@RequestMapping(value={"/secure/core/les/les-templates"})
public class CoreLesTemplateResourceImpl
implements CoreLesTemplateResource,
AbstractGenericResource<CoreLesTemplateService, CoreLesTemplateBean, Long>,
AbstractGenericAuditableResource<CoreLesTemplateService, CoreLesTemplateBean, Long>,
AbstractGenericChoosableResource<CoreLesTemplateService, CoreLesTemplateBean, Long> {
    @Autowired
    private CoreLesTemplateService templateService;
    @Autowired
    private CoreLesTemplateOrgService coreLesTemplateOrgService;

    @Override
    public CoreLesTemplateService getService() {
        return this.templateService;
    }

    @Override
    @Log(value="\u65b0\u589eLES\u6a21\u677f", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664LES\u6a21\u677f", type=LogType.INSERT)
    @ValidateDataWith(value=CoreLesDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u65b0\u7248\u672cLES\u6a21\u677f", type=LogType.INSERT)
    public Long newVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.templateService.newVersion(id, wrapper);
    }

    @Override
    @Log(value="\u590d\u5236LES\u6a21\u677f", type=LogType.INSERT)
    public Long copy(@PathVariable Long copiedId, RestJsonWrapperBean wrapper) {
        return this.templateService.copy(copiedId, wrapper);
    }

    @Override
    @Log(value="\u8bbe\u7f6eLES\u6a21\u677f\u8fc7\u671f", type=LogType.UPDATE)
    public void expireTemplate(RestJsonWrapperBean wrapper) {
        this.templateService.expireTemplate(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589eLES\u6a21\u677f\u7684\u7ec4\u4ef6", type=LogType.INSERT)
    public Long insertUnit(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertUnit(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u6a21\u677f\u7684\u7ec4\u4ef6", type=LogType.SELECT)
    public CoreLesTemplateUnitBean selectUnit(String id, Long unitId) {
        return this.getService().selectUnit(id, unitId);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u6a21\u677f\u7ec4\u4ef6\u7684\u6811\u7ed3\u6784", type=LogType.SELECT)
    public Pagination<CoreLesTemplateUnitDTO> selectUnitTreePagination(String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectUnitTreePagination(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58LES\u6a21\u677f\u7ec4\u4ef6", type=LogType.UPDATE)
    public void saveUnit(String id, Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().saveUnit(id, unitId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664LES\u6a21\u677f\u7ec4\u4ef6", type=LogType.DELETE)
    public void deleteUnit(String id, Long unitId) {
        this.getService().deleteUnit(id, unitId);
    }

    @Override
    @Log(value="\u6a21\u677f\u7ec4\u4ef6\u4ea4\u6362\u987a\u5e8f", type=LogType.UPDATE)
    public void swapUnit(String id, Long firstId, @PathVariable Long secondId) {
        this.getService().swapUnit(firstId, secondId);
    }

    @Override
    @Log(value="\u7ae0\u8282\u7ec4\u4ef6\u5347\u7ea7\u64cd\u4f5c", type=LogType.UPDATE)
    public void dedentSection(Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().dedentSection(unitId, wrapper);
    }

    @Override
    @Log(value="\u7ae0\u8282\u7ec4\u4ef6\u964d\u7ea7\u64cd\u4f5c", type=LogType.UPDATE)
    public void indentSection(Long unitId, Long parentId, RestJsonWrapperBean wrapper) {
        this.getService().indentSection(unitId, parentId, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u6a21\u677f\u4e2d\u5168\u90e8\u7ec4\u4ef6", type=LogType.SELECT)
    public Pagination<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectLesFilePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2LES\u6a21\u677f\u4f7f\u7528\u5355\u4f4d", type=LogType.SELECT)
    public Page<CoreLesTemplateOrgBean> selectTemplateOrgPagation(RestJsonWrapperBean wrapper) {
        return this.coreLesTemplateOrgService.selectPagination(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589eLES\u6a21\u677f\u4f7f\u7528\u90e8\u95e8", type=LogType.INSERT)
    public void insertTemplateOrg(RestJsonWrapperBean wrapper) {
        this.coreLesTemplateOrgService.insertTemplateOrg(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664LES\u6a21\u677f\u4f7f\u7528\u90e8\u95e8", type=LogType.DELETE)
    public void deleteTemplateOrg(RestJsonWrapperBean wrapper) {
        this.coreLesTemplateOrgService.delete(wrapper);
    }

    @Override
    @Log(value="LES\u6a21\u677f\u63d0\u4ea4\u6821\u9a8c", type=LogType.VALIDATE)
    public RestValidationResultBean submitValidate(RestJsonWrapperBean wrapper) {
        return this.getService().submitValidate(wrapper);
    }
}
