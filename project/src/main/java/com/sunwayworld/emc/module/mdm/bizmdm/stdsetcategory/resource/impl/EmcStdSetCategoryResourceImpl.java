package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcFieldSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.resource.EmcStdSetCategoryResource;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStdSetCategoryService;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("标准设置分类")
@RestController
@GikamBean
public class EmcStdSetCategoryResourceImpl implements EmcStdSetCategoryResource {
    @Autowired
    private EmcStdSetCategoryService emcStdSetCategoryService;

    @Override
    public EmcStdSetCategoryService getService() {
        return emcStdSetCategoryService;
    }

    @Log(value = "新增标准设置分类", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "新增标准设置", type = LogType.INSERT)
    @Override
    public Long insertStandardSet(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return emcStdSetCategoryService.insertStandardSet(id, wrapper);
    }

    @Log(value = "查询标准设置", type = LogType.SELECT)
    @Override
    public Page<EmcStandardSetBean> selectStandardSetPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return emcStdSetCategoryService.selectStandardSetPaginationById(id, wrapper);
    }

    @Log(value = "删除标准设置", type = LogType.DELETE)
    @Override
    public void deleteStandardSet(RestJsonWrapperBean wrapper) {
        emcStdSetCategoryService.deleteStandardSet(wrapper);
    }

    @Log(value = "标准设置详细信息查询", type = LogType.SELECT)
    @Override
    public EmcStandardSetBean selectStandardSetDetail(@PathVariable Long standSetId) {
        return getService().selectStandardSetDetail(standSetId);
    }

    @Log(value = "新增字段设置", type = LogType.INSERT)
    @Override
    public Long insertFieldSet(@PathVariable Long standardSetId, RestJsonWrapperBean wrapper) {
        return emcStdSetCategoryService.insertFieldSet(standardSetId, wrapper);
    }

    @Log(value = "查询字段设置", type = LogType.SELECT)
    @Override
    public Page<EmcFieldSetBean> selectFieldSetPaginationByStandardSetId(@PathVariable Long standardSetId, RestJsonWrapperBean wrapper) {
        return emcStdSetCategoryService.selectFieldSetPaginationByStandardSetId(standardSetId, wrapper);
    }

    @Log(value = "删除字段设置", type = LogType.DELETE)
    @Override
    public void deleteFieldSet(RestJsonWrapperBean wrapper) {
        emcStdSetCategoryService.deleteFieldSet(wrapper);
    }

    @Log(value = "查询字段信息", type = LogType.SELECT)
    @Override
    public Page<BeanPropertyDescriptor> selectFieldInfoPagination(RestJsonWrapperBean wrapper) {
        return emcStdSetCategoryService.selectFieldInfoPagination(wrapper);
    }

    @Log(value = "标准设置下拉框数据查询", type = LogType.SELECT)
    @Override
    public List<EmcStandardSetBean> selectStandardSetCategorySelectableList() {
        return getService().selectStandardSetCategorySelectableList();
    }

    @Override
    @Log(value = "标准设置字段查询", type = LogType.SELECT)
    public List<EmcFieldSetBean> selectFieldSetList(RestJsonWrapperBean wrapper) {
        return getService().selectFieldSetList(wrapper);
    }
}
