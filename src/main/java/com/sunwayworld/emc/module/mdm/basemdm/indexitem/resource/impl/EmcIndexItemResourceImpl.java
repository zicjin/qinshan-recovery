package com.sunwayworld.emc.module.mdm.basemdm.indexitem.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.resource.EmcIndexItemResource;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexItemService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

@LogModule("指标项目")
@RestController
@GikamBean
public class EmcIndexItemResourceImpl implements EmcIndexItemResource {
    @Autowired
    private EmcIndexItemService indexItemService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Override
    public EmcIndexItemService getService() {
        return indexItemService;
    }

    @Log(value = "新增指标项目", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询指标项目分析项", type = LogType.SELECT)
    public Page<EmcIndexTestItemBean> selectIndexTestItemPaginationByIndexItemId(Long id, RestJsonWrapperBean wrapper) {
        return indexTestItemService.selectPaginationByFilter(SearchFilter.instance().match("INDEXITEMID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询指标项目分析项的指标高低限", type = LogType.SELECT)
    public Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper) {
        return indexItemService.selectIndexTestItemLimit(wrapper);
    }

    @Override
    @Log(value = "新增指标项目分析项", type = LogType.INSERT)
    public Long insertIndexTestItem(Long id, RestJsonWrapperBean wrapper) {
        return indexTestItemService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除指标项目分析项", type = LogType.DELETE)
    public void deleteIndexTestItem(Long id, RestJsonWrapperBean wrapper) {
        indexTestItemService.delete(wrapper);
    }
}
