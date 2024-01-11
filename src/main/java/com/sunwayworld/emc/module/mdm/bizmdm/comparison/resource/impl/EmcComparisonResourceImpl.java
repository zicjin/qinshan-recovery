package com.sunwayworld.emc.module.mdm.bizmdm.comparison.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.resource.EmcComparisonResource;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.EmcComparisonService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@GikamBean
@RestController
@LogModule(value = "比对信息")
public class EmcComparisonResourceImpl implements EmcComparisonResource {

    @Autowired
    private EmcComparisonService emcComparisonService;

    @Override
    public EmcComparisonService getService() {
        return emcComparisonService;
    }

    @Override
    @Log(value = "查询比对结果", type = LogType.SELECT)
    @RequestMapping(value = "/results/queries", method = RequestMethod.POST)
    public Page<EmcComparisonResultBean> selectResultPagination(RestJsonWrapperBean wrapper) {
        return emcComparisonService.selectResultPagination(wrapper);
    }

    @Override
    @Log(value = "新增比对结果", type = LogType.INSERT)
    @RequestMapping(value = "/results/{folderId}/action/insert", method = RequestMethod.POST)
    public void insertResultByFolder(@PathVariable Long folderId) {
        emcComparisonService.insertResultByFolder(folderId);
    }
}
