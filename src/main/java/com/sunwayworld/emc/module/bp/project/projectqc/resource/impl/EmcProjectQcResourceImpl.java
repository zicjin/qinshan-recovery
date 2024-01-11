package com.sunwayworld.emc.module.bp.project.projectqc.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcBean;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcLineBean;
import com.sunwayworld.emc.module.bp.project.projectqc.resource.EmcProjectQcResource;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@GikamBean
@RestController
@LogModule(value = "项目质控")
public class EmcProjectQcResourceImpl implements EmcProjectQcResource {

    @Autowired
    private EmcProjectQcService emcProjectQcService;

    @Override
    public EmcProjectQcService getService() {
        return emcProjectQcService;
    }

    @Override
    @Log(value = "样品类型查询", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/order-categories/queries", method = RequestMethod.POST)
    public Page<EmcProjectQcBean> selectOrderCategoryPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderCategoryPagination(id, wrapper);
    }

    @Override
    @Log(value = "项目质控明细查询", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/order-categories/{monitorTmplId}/lines/queries", method = RequestMethod.POST)
    public Page<EmcProjectQcLineBean> selectQcLinePagination(@PathVariable Long id, @PathVariable String monitorTmplId, RestJsonWrapperBean wrapper) {
        return null;
    }

    @Override
    @Log(value = "项目质控明细表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/lines/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectQcLineTitleList(RestJsonWrapperBean wrapper) {
        return getService().selectQcLineTitleList(wrapper);
    }

    @Override
    @Log(value = "项目质控明细表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/lines/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectQcLineRowList(RestJsonWrapperBean wrapper) {
        return getService().selectQcLineRowList(wrapper);
    }
}
