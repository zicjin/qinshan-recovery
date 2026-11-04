/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitDTO;
import com.sunwayworld.cloud.module.les.resource.CoreLesInstanceResource;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceService;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceUnitService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.AbstractGenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="LES\u5b9e\u4f8b")
@GikamBean
@RequestMapping(value={"/secure/core/les/les-instances"})
public class CoreLesInstanceResourceImpl
implements CoreLesInstanceResource,
AbstractGenericResource<CoreLesInstanceService, CoreLesInstanceBean, Long>,
AbstractGenericAuditableResource<CoreLesInstanceService, CoreLesInstanceBean, Long> {
    @Autowired
    private CoreLesInstanceService instanceService;
    @Autowired
    private CoreLesInstanceUnitService instanceUnitService;

    @Override
    public CoreLesInstanceService getService() {
        return this.instanceService;
    }

    @Override
    @Log(value="\u901a\u8fc7LES\u6a21\u677f\u6765\u65b0\u589e\u5b9e\u4f8b", type=LogType.INSERT)
    public Long insertByTemplateId(@PathVariable Long templateId, RestJsonWrapperBean wrapper) {
        return this.getService().insertByTemplateId(templateId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589eLES\u5b9e\u4f8b\u7684\u7ec4\u4ef6", type=LogType.INSERT)
    public Long insertUnit(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertUnit(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u5b9e\u4f8b\u7684\u7ec4\u4ef6", type=LogType.SELECT)
    public CoreLesInstanceUnitBean selectUnit(@PathVariable Long id, @PathVariable Long unitId) {
        return this.getService().selectUnit(id, unitId);
    }

    @Override
    @Log(value="\u4fdd\u5b58LES\u5b9e\u4f8b\u7ec4\u4ef6", type=LogType.UPDATE)
    public void saveUnit(@PathVariable Long id, @PathVariable Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().saveUnit(id, unitId, wrapper);
    }

    @Override
    @Log(value="\u5237\u65b0LES\u5b9e\u4f8b\u7ec4\u4ef6", type=LogType.UPDATE)
    public void refreshUnit(@PathVariable Long id, @PathVariable Long unitId, RestJsonWrapperBean wrapper) {
        this.instanceUnitService.refreshUnit(unitId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664LES\u5b9e\u4f8b\u7ec4\u4ef6", type=LogType.DELETE)
    public void deleteUnit(@PathVariable Long id, @PathVariable Long unitId) {
        this.getService().deleteUnit(id, unitId);
    }

    @Override
    @Log(value="\u6a21\u677f\u7ec4\u4ef6\u4ea4\u6362\u987a\u5e8f", type=LogType.UPDATE)
    public void swapUnit(@PathVariable Long id, @PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapUnit(firstId, secondId);
    }

    @Override
    @Log(value="\u7ae0\u8282\u7ec4\u4ef6\u5347\u7ea7\u64cd\u4f5c", type=LogType.UPDATE)
    public void dedentSection(@PathVariable Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().dedentSection(unitId, wrapper);
    }

    @Override
    @Log(value="\u7ae0\u8282\u7ec4\u4ef6\u964d\u7ea7\u64cd\u4f5c", type=LogType.UPDATE)
    public void indentSection(@PathVariable Long unitId, @PathVariable Long parentId, RestJsonWrapperBean wrapper) {
        this.getService().indentSection(unitId, parentId, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u5b9e\u4f8b\u7ec4\u4ef6\u7684\u6811\u7ed3\u6784", type=LogType.SELECT)
    public Pagination<CoreLesInstanceUnitDTO> selectUnitTreePagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectUnitTreePagination(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6LES\u5b9e\u4f8b\u4e2d\u5168\u90e8\u7ec4\u4ef6", type=LogType.SELECT)
    public Pagination<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectLesFilePagination(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u7ec4\u4ef6ID\u67e5\u8be2\u7ec4\u4ef6\u6570\u636e", type=LogType.SELECT)
    public List<Map<String, Object>> selectLesUnitDataByUnitId(Long unitId, RestJsonWrapperBean wrapper) {
        return this.instanceService.selectLesUnitDataByUnitId(unitId, wrapper);
    }

    @Override
    @Log(value="\u7c98\u8d34LES\u5b9e\u4f8b\u7684\u7ec4\u4ef6", type=LogType.INSERT)
    public Long pasteUnit(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().pasteUnit(id, wrapper);
    }
}
