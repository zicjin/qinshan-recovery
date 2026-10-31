package com.sunwayworld.emc.module.bp.info.infotmpl.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.resource.EmcInfoTmplResource;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@LogModule("上报模板")
@RestController
@GikamBean
public class EmcInfoTmplResourceImpl implements EmcInfoTmplResource {
    @Autowired
    private EmcInfoTmplService infoTmplService;

    @Override
    public EmcInfoTmplService getService() {
        return infoTmplService;
    }

    @Override
    @Log(value = "新增上报模板", type = LogType.INSERT)
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
    
    @Override
    @Log(value = "复制上报模板", type = LogType.INSERT)
    @RequestMapping(value = "/{tmplId}/copy", method = RequestMethod.POST)
    public EmcInfoTmplBean copy(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return getService().copy(tmplId);
    }

    @Override
    @Log(value = "查询上报模板字段项", type = LogType.SELECT)
    @RequestMapping(value = "/{tmplId}/fields/queries", method = RequestMethod.POST)
    public Page<EmcInfoTmplFieldBean> selectTmplFieldPaginationByTmplId(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return getService().selectTmplFieldPaginationByTmplId(tmplId, wrapper);
    }
    
    @Override
    @Log(value = "综合信息模板字段下拉框数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/fields/queries/selectable", method = RequestMethod.GET)
    public List<EmcInfoTmplFieldBean> selectTmplFieldSelectableList() {
        return getService().selectTmplFieldSelectableList();
    }
    
    @Override
    @Log(value = "综合信息模板字段数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/fields/queries/raw", method = RequestMethod.GET)
    public Page<EmcInfoTmplFieldBean> selectTmplFieldPagination(RestJsonWrapperBean wrapper) {
        return getService().selectTmplFieldPagination(wrapper);
    }
    @Override
    @Log(value = "查询上报模板字段分析项", type = LogType.SELECT)
    @RequestMapping(value = "/items/queries", method = RequestMethod.POST)
    public Page<EmcTmplFieldItemBean> selectTmplFieldItemPaginationByTmplId(RestJsonWrapperBean wrapper) {
        return getService().selectTmplFieldItemPaginationByTmplId(wrapper);
    }
    
    @Override
    @Log(value = "新增上报模板字段分析项", type = LogType.INSERT)
    @RequestMapping(value = "/{tmplId}/items", method = RequestMethod.POST)
    public Long insertTmplFieldItem(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return getService().insertTmplFieldItem(tmplId, wrapper);
    }
    
    @Override
    @Log(value = "删除上报模板字段分析项", type = LogType.DELETE)
    @RequestMapping(value = "/items", method = RequestMethod.DELETE)
    public void deleteTmplFieldItem(RestJsonWrapperBean wrapper) {
        getService().deleteTmplFieldItem(wrapper);
    }
    
    
    @Override
    @Log(value = "根据上报模板编码查询监测点", type = LogType.SELECT)
    @RequestMapping(value = "/{tmplId}/points/queries", method = RequestMethod.POST)
    public Page<EmcInfoTmplPointBean> selectTmplPointPaginationByTmplId(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return getService().selectTmplPointPaginationByTmplId(tmplId, wrapper);
    }
    
    @Override
    @Log(value = "查询上报模板监测点", type = LogType.SELECT)
    @RequestMapping(value = "/points/queries", method = RequestMethod.POST)
    public Page<EmcInfoTmplPointBean> selectTmplPointPagination(RestJsonWrapperBean wrapper) {
        return getService().selectTmplPointPagination(wrapper);
    }
    
    @Override
    @Log(value = "新增单个上报模板的多个监测点数据", type = LogType.INSERT)
    @RequestMapping(value = "/points", method = RequestMethod.POST)
    public void insertTmplPoint(RestJsonWrapperBean wrapper) {
        getService().insertTmplPoint(wrapper);
    }
    
    @Override
    @Log(value = "删除单个上报模板的多个监测点", type = LogType.DELETE)
    @RequestMapping(value = "/points", method = RequestMethod.DELETE)
    public void deleteTmplPoint(RestJsonWrapperBean wrapper) {
        getService().deleteTmplPoint(wrapper);
    }
        
    @Override
    @Log(value = "更新多个上报模板的多个监测点数据", type = LogType.INSERT)
    @RequestMapping(value = "/points", method = RequestMethod.PUT)
    public void updateTmplPoint(RestJsonWrapperBean wrapper) {
        getService().updateTmplPoint(wrapper);
    }
    
}
