package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.resource.EmcDataCorrelationResource;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@LogModule("数据相关性设置")
@RestController
@GikamBean
public class EmcDataCorrelationResourceImpl implements EmcDataCorrelationResource {
    @Autowired
    private EmcDataCorrelationService dataCorrelationService;

    @Override
    public EmcDataCorrelationService getService() {
        return dataCorrelationService;
    }

    @Override
    @Log(value = "新增数据相关性设置", type = LogType.INSERT)
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "通过数据相关性查询数据相关性分析项", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/items/queries", method = RequestMethod.POST)
    public Page<EmcDataCorrelationItemBean> selectDataCorrelationItemByCorrelationId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectDataCorrelationItemByCorrelationId(id, wrapper);
    }
    
    
    @Override
    @Log(value = "通过数据相关性查询可选择的分析项", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/items/chooseable", method = RequestMethod.POST)
    public Page<EmcDataCorrelationItemBean> selectChooseableDataCorrelationItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectChooseableDataCorrelationItem(id, wrapper);
    }

    @Override
    @Log(value = "更新数据相关性分析项", type = LogType.UPDATE)
    @RequestMapping(value = "/{id}/items", method = RequestMethod.PUT)
    public void updateDataCorrelationItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().updateDataCorrelationItem(id, wrapper);
    }
}
