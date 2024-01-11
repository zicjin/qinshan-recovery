package com.sunwayworld.emc.module.bp.edit.projectchange.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.resource.EmcProjectChangeResource;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@LogModule("项目")
@RestController
@GikamBean
public class EmcProjectChangeResourceImpl implements EmcProjectChangeResource {
    @Autowired
    private EmcProjectChangeService projectService;

    @Override
    public EmcProjectChangeService getService() {
        return projectService;
    }

    @Log(value = "新增项目", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "根据项目ID查询样品", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/orders/queries", method = RequestMethod.POST)
    public Page<EmcOrderChangeBean> selectOrderPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderPaginationByProjectId(id, wrapper);
    }

    @Override
    @Log(value = "新增样品", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.POST)
    public Long insertOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertOrder(id, wrapper);
    }

    @Override
    @Log(value = "删除样品", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteOrder(id, wrapper);
    }

    @Override
    @Log(value = "复制为一般样品-样品登记", type = LogType.INSERT)
    @RequestMapping(value = "/orders/actions/copy", method = RequestMethod.POST)
    public void copeOrder(RestJsonWrapperBean jsonWrapper) {
        getService().copeOrder(jsonWrapper);
    }

    @Override
    @Log(value = "复制为QC样品-样品登记", type = LogType.INSERT)
    @RequestMapping(value = "/orders/actions/copy-qc", method = RequestMethod.POST)
    public void copeQcOrder(RestJsonWrapperBean jsonWrapper) {
        getService().copeQcOrder(jsonWrapper);
    }

    @Override
    @Log(value = "项目查询", type = LogType.SELECT)
    public Page<EmcProjectChangeBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSearchablePagination(wrapper);
    }

    @Override
    @Log(value = "根据项目编号/名称查询项目", type = LogType.SELECT)
    @RequestMapping(value = "/{keyword}/queries", method = RequestMethod.GET)
    public Long selectIdByKeyword(@PathVariable String keyword, RestJsonWrapperBean wrapper) {
        return getService().selectIdByKeyword(keyword);
    }

    @Override
    @Log(value = "根据项目编号查询项目负责人", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/directors/queries", method = RequestMethod.POST)
    public Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectDirectorPaginationByProjectId(id, wrapper);
    }

    @Override
    @Log(value = "新增项目负责人", type = LogType.INSERT)
    @RequestMapping(value = "/directors", method = RequestMethod.POST)
    public void insertDirector(RestJsonWrapperBean wrapper) {
        getService().insertDirector(wrapper);
    }

    @Override
    @Log(value = "删除项目负责人", type = LogType.DELETE)
    @RequestMapping(value = "/directors", method = RequestMethod.DELETE)
    public void deleteDirector(RestJsonWrapperBean wrapper) {
        getService().deleteDirector(wrapper);
    }

    @Override
    @Log(value = "查询项目详情", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/detail", method = RequestMethod.POST)
    public EmcProjectChangeBean selectProjectDetail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectProjectDetail(id, wrapper);
    }
}
