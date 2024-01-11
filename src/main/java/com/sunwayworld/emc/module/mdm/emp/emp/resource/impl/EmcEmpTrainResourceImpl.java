package com.sunwayworld.emc.module.mdm.emp.emp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.emc.module.mdm.emp.emp.resource.EmcEmpTrainResource;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpTrainService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@RestController
@GikamBean
public class EmcEmpTrainResourceImpl extends EmcEmpResourceImpl implements EmcEmpTrainResource{
    
    @Autowired
    private EmcEmpTrainService emcEmpTrainService;

    @Log(value = "新增人员培训", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/trains", method = RequestMethod.POST)
    public void insertTrain(@PathVariable String id, RestJsonWrapperBean wrapper) {
        emcEmpTrainService.insertTrain(id, wrapper);
    }

    @Log(value = "通过人员查询培训记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/trains/queries", method = RequestMethod.POST)
    public Page<EmcEmpTrainBean> selectTrainPagationByEmp(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return emcEmpTrainService.selectPaginationByFilter(SearchFilter.instance().match("EMPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "删除人员培训", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/trains", method = RequestMethod.DELETE)
    public void deleteTrain(@PathVariable String id, RestJsonWrapperBean wrapper) {
        emcEmpTrainService.delete(wrapper);	
    }

}
