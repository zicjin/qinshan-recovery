package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizClassBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.resource.EmcBizCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizClassService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "业务类型")
@RestController
@GikamBean
public class EmcBizCategoryResourceImpl implements EmcBizCategoryResource{

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private EmcBizClassService bizClassService;
    
    @Override
    public EmcBizCategoryService getService() {
        return bizCategoryService;
    }

    @Override
    @Log(value = "新增项目", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/projects", method = RequestMethod.POST)
    public void insertProject(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().insertProject(id, wrapper);
    }

    @Override
    @Log(value = "通过业务类型id查询项目", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/projects/queries", method = RequestMethod.POST)
    public Page<EmcBizCategoryProjectBean> selectBizCategoryProjectPaginationByBizCategoryId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectBizCategoryProjectPaginationByBizCategoryId(id, wrapper);
    }

    @Override
    @Log(value = "删除项目", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/projects", method = RequestMethod.DELETE)
    public void deleteProject(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteProject(id, wrapper);
    }

    @Override
    @Log(value = "业务类型常用项目查询", type = LogType.SELECT)
    @RequestMapping(value = "/projects/queries", method = RequestMethod.POST)
    public Page<EmcBizCategoryProjectBean> selectBizCategoryProjectChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectBizCategoryProjectChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "业务类型常用项目下拉查询", type = LogType.SELECT)
    @RequestMapping(value = "/projects/queries/selectable", method = RequestMethod.GET)
    public List<EmcBizCategoryProjectBean> selectProjectSelectableList() {
        return getService().selectProjectSelectableList();
    }

    @Override
    @Log(value = "新增业务类别", type = LogType.INSERT)
    @RequestMapping(value = "/biz-class", method = RequestMethod.POST)
    public void insertBizClass(RestJsonWrapperBean wrapper) {
        bizClassService.insert(wrapper);
    }

    @Override
    @Log(value = "删除业务类别", type = LogType.DELETE)
    @RequestMapping(value = "/biz-class", method = RequestMethod.DELETE)
    public void deleteBizClass(RestJsonWrapperBean wrapper) {
        bizClassService.delete(wrapper);
    }

    @Override
    @Log(value = "业务类别查询", type = LogType.SELECT)
    @RequestMapping(value = "/biz-class/queries", method = RequestMethod.POST)
    public Page<EmcBizClassBean> selectBizClass(RestJsonWrapperBean wrapper) {
        return bizClassService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "业务类别下拉框数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/biz-class/queries/selectable", method = RequestMethod.GET)
    public List<EmcBizClassBean> selectBizClassSelectableList() {
        return bizClassService.selectSelectableList();
    }

}
